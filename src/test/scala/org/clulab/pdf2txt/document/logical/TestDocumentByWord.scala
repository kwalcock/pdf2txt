package org.clulab.pdf2txt.document.logical

import org.clulab.pdf2txt.common.utils.{Test, TextRanges}

class TestDocumentByWord extends Test {

  behavior of "DocumentByWord"

  val inputText = "This is a paragraph.  It has two sentences.\n\nThis is another paragraph.  It also has two sentences."

  it should "know its content" in {
    val document = DocumentByWord(inputText)
    val outputText = document.foldLeft(new StringBuilder()) { case (stringBuilder, char) =>
      stringBuilder += char
    }.toString

    outputText shouldBe inputText
  }

  it should "know its children" in {
    val document = DocumentByWord(inputText)
    val outputText = document.getChildren.foldLeft(new StringBuilder()) { case (stringBuilder, textRange) =>
      stringBuilder ++= textRange.toString
    }.toString

    outputText shouldBe inputText
  }

  it should "know its separators and content" in {
    val document = DocumentByWord(inputText)
    val textRanges = new TextRanges()

    textRanges += document.preSeparator
    textRanges ++= document.contents
    textRanges += document.postSeparator

    val outputText = textRanges.toString

    outputText shouldBe inputText
  }
}
