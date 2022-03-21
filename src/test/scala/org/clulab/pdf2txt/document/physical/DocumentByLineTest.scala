package org.clulab.pdf2txt.document.physical

import org.clulab.pdf2txt.common.utils.{Test, TextRanges}

class DocumentByLineTest extends Test {

  behavior of "DocumentByLine"

  val inputText = "\nThis\n\nis a\r\ntest.\n"

  it should "know its content" in {
    val document = DocumentByLine(inputText)
    val outputText = document.foldLeft(new StringBuilder()) { case (stringBuilder, char) =>
      stringBuilder += char
    }.toString

    outputText shouldBe inputText
  }

  it should "know its children" in {
    val document = DocumentByLine(inputText)
    val outputText = document.getChildren.foldLeft(new StringBuilder()) { case (stringBuilder, textRange) =>
      stringBuilder ++= textRange.toString
    }.toString

    outputText shouldBe inputText
  }

  it should "know its separators and content" in {
    val document = DocumentByLine(inputText)
    val textRanges = new TextRanges()

    textRanges += document.preSeparator
    textRanges ++= document.contents
    textRanges += document.postSeparator

    val outputText = textRanges.toString

    outputText shouldBe inputText
    document.contents.length shouldBe 5
  }
}
