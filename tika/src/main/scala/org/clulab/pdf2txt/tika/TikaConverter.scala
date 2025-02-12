package org.clulab.pdf2txt.tika

import org.apache.tika.config.TikaConfig
import org.apache.tika.detect.Detector
import org.apache.tika.exception.WriteLimitReachedException
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.sax.BodyContentHandler
import org.clulab.pdf2txt.common.pdf.PdfConverter
import org.clulab.pdf2txt.common.utils.Closer.AutoCloser
import org.clulab.pdf2txt.common.utils.MetadataHolder

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}

class TikaConverter(config: TikaConfig = new TikaConfig()) extends PdfConverter {
  val detector: Detector = config.getDetector
  val parser = new AutoDetectParser(config)

  def isPdf(inputStream: InputStream): Boolean = {
    val metadata = new Metadata()
    val mediaType = detector.detect(inputStream, metadata)

    inputStream.reset()
    mediaType.toString == "application/pdf"
  }

  def read(inputStream: InputStream): String = {
    val handler = new BodyContentHandler(-1)
    val metadata = new Metadata()

    if (isPdf(inputStream)) {
      try {
        parser.parse(inputStream, handler, metadata)
        handler.toString()
      }
      catch {
        case exception: WriteLimitReachedException =>
          throw exception
        case exception: NoClassDefFoundError =>
          throw exception
      }
    }
    else
      throw new RuntimeException("Not PDF!")
  }

  override def convert(file: File, metadataHolderOpt: Option[MetadataHolder] = None): String = {
    // The InputStream must support mark/reset which isn't enforced by the type system.
    // In other words, a simple FileInputStream will throw an exception at runtime.
    new BufferedInputStream(new FileInputStream(file)).autoClose { inputStream =>
      read(inputStream)
    }
  }
}
