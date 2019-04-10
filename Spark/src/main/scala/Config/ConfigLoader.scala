package Config


import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
class ConfigLoader(val configFile: String) {


  private val config = ConfigFactory.parseFile(new File(configFile))

  object sampleConfig {

    private val sample = config.getConfig("sample")

    lazy val urlSrc = sample.getString("urlSrc")
    lazy val userSrc = sample.getString("userSrc")
    lazy val passwordSrc = sample.getString("passwordSrc")

    lazy val urlDest = sample.getString("urlDest")
    lazy val userDest = sample.getString("userDest")
    lazy val passwordDest = sample.getString("passwordDest")

    lazy val outputPath = sample.getString("outputPath")

    lazy val percent = sample.getDouble("percent")
    lazy val filter = sample.getString("filter")

  }

  object MembersConfig1 {
    private val memConfig = config.getConfig("GenerateMember1")

    lazy val records = memConfig.getInt("records")
    lazy val filePath = memConfig.getString("file_path")
    lazy val layout = memConfig.getString("layout")

  }

  object MembersConfig2 {
    private val memConfig = config.getConfig("GenerateMember2")

    lazy val records = memConfig.getInt("records")
    lazy val filePath = memConfig.getString("file_path")
    lazy val layout = memConfig.getString("layout")

  }


}
