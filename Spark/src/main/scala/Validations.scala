import org.apache.spark.sql.types._

object Regex {
  val INT_PATTERN: String = "^[-+]?[0-9]*$"
  val FLOAT_PATTERN: String = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$"
  val DATE_PATTERN: String = "^(\\d{4})([-])(0[1-9]|1[0-2])([-])([12]\\d|0[1-9]|3[01])(\\D?([01]\\d|2[0-3])\\D?([0-5]\\d)\\D?([0-5]\\d)?\\D?(\\d{3})?)?$"

  implicit class RegExHelper(value: String){
    import scala.util.matching.Regex
    def isValidFormat(pattern: String)= value.matches(pattern)

  }
}


object Validations {

  def convertTypes(value: String, struct: StructField): Any = struct.dataType match {
    case DoubleType => if(!value.isEmpty && formatValidator(value, struct)) value.toDouble else null
    case FloatType => if(!value.isEmpty && formatValidator(value, struct)) value.toFloat else null
    case DateType => if(!value.isEmpty && formatValidator(value, struct)) java.sql.Date.valueOf(value.toString) else null
    case IntegerType => if(!value.isEmpty && formatValidator(value, struct)) value.toInt else null
    case LongType => if(!value.isEmpty && formatValidator(value, struct)) value.toLong else null
    case _ => if(!value.isEmpty && formatValidator(value, struct)) value else null
  }

  def formatValidator(value : String, struct: StructField) : Boolean = {
    if (value == null || StringType.equals(struct.dataType)) {
      //do nothing
    } else if (IntegerType.equals(struct.dataType)) {
      if (!value.toString.matches(Regex.INT_PATTERN)) {
        throw new RuntimeException("Invalid format for field: " + struct.name + " Type: " + struct.dataType + " Value: " + value)
      }
    } else if (DateType.equals(struct.dataType)) {
      if (!value.toString.matches(Regex.DATE_PATTERN)) {
        throw new RuntimeException("Invalid format for field: " + struct.name + " Type: " + struct.dataType + " Value: " + value)
      }
    } else if (DoubleType.equals(struct.dataType)) {
      if (!value.toString.matches(Regex.FLOAT_PATTERN)) {
        throw new RuntimeException("Invalid format for field: " + struct.name + " Type: " + struct.dataType + " Value: " + value)
      }
    } else if (LongType.equals(struct.dataType)) {
      if (!value.toString.matches(Regex.INT_PATTERN)) {
        throw new RuntimeException("Invalid format for field: " + struct.name + " Type: " + struct.dataType + " Value: " + value)
      }
    } else if (FloatType.equals(struct.dataType)) {
      if (!value.toString.matches(Regex.FLOAT_PATTERN)) {
        throw new RuntimeException("Invalid format for field: " + struct.name + " Type: " + struct.dataType + " Value: " + value)
      }
    }
    true
  }
}


