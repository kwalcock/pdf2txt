package org.clulab.pdf2txt.apps

import org.clulab.pdf2txt.common.utils.Pdf2txtAppish

object AmazonApp extends Pdf2txtAppish {
  new Pdf2txtApp(args, Map(Pdf2txtArgs.CONVERTER -> Pdf2txtArgs.AMAZON)).run()
}
