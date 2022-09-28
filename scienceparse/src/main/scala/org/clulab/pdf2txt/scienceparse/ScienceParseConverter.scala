package org.clulab.pdf2txt.scienceparse

import org.allenai.scienceparse.{ExtractedMetadata, Parser}
import org.clulab.pdf2txt.common.pdf.PdfConverter
import org.clulab.pdf2txt.common.utils.Closer.AutoCloser
import org.clulab.pdf2txt.common.utils.{MetadataHolder, TextRange}

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class ScienceParseConverter(scienceParseSettings: ScienceParseSettings = ScienceParseConverter.defaultSettings) extends PdfConverter {
  override val metaExtension: String = ".json"

  val parser: Parser = {
    val parserOpt = Option(Parser.getInstance())

    parserOpt.getOrElse(throw new RuntimeException("ScienceParse returned a null parser instance."))
  }
  val paragraphPreprocessor = new ParagraphPreprocessor()

  def toString(extractedMetadata: ExtractedMetadata): String = {
    val stringBuilder = new StringBuilder()

    def append(textOrNull: String): Unit = {
      val textOpt = Option(textOrNull)

      textOpt.map { text =>
        val textRanges = paragraphPreprocessor.preprocess(TextRange(text))

        textRanges.toString(stringBuilder)
      }
    }

    append(extractedMetadata.title)
    append(extractedMetadata.abstractText)
    Option(extractedMetadata.sections).map(_.asScala).getOrElse(List.empty).foreach { section =>
      append(section.heading)
      append(section.text)
    }
    stringBuilder.toString
  }

  def read(inputStream: InputStream): String = {
    val extractedMetadataOpt = Option(parser.doParse(inputStream))

    extractedMetadataOpt.map(toString).getOrElse("")
  }

  override def convert(file: File, metadataHolderOpt: Option[MetadataHolder] = None): String = {
    new BufferedInputStream(new FileInputStream(file)).autoClose { inputStream =>
      read(inputStream)
    }
  }
}

case class ScienceParseSettings()

object ScienceParseConverter {
  val defaultSettings: ScienceParseSettings = ScienceParseSettings()
}
