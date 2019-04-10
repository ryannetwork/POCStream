package Fakers

import java.util.Date
import java.util.concurrent.TimeUnit

import org.apache.spark.sql.functions.udf
import org.joda.time.DateTime

import scala.util.Random

object FakerData
  extends BasedFaker {

  val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")

  def futureDate(day: Int): DateTime =
    new DateTime(faker.date.future(day, TimeUnit.SECONDS, new Date))

  def futureDateRandom: DateTime =
    new DateTime(faker.date.future(21 - Random.nextInt(20)
      , TimeUnit.SECONDS, new Date))

  def dob: String = dateFormat.format(faker.date.birthday())

  def email: String = faker.internet.emailAddress()

  def rndValue (value: String ): String = value.toLowerCase match {
    case "firstname" => faker.name.firstName
    case "lastname" => faker.name.lastName
    case "address" =>  faker.address.streetAddress(false)
    case "phone" => faker.phoneNumber.phoneNumber //.filter(_.isDigit)
    case "city" => faker.address.city
    case "state"  => faker.address.stateAbbr
    case "zip"  => faker.address.zipCode
    case "memberid" => faker.code.isbn13
    case "providerid" => faker.code.isbn13
    case "dob" => dob.filter(_.isDigit)
    case "hicn" => faker.number.digits(9)
    case "tin" => faker.number.digits(10)
    case "npi" => faker.number.digits(9)
    case _ =>  null
  }

  val randFirstName = udf((value: String) => FakerData.faker.name.firstName)
  val randLastName = udf((value: String) => FakerData.faker.name.lastName)
  val randPhone = udf((value: String) => FakerData.faker.phoneNumber.phoneNumber.filter(_.isDigit))
  val randAddress1 = udf((value: String) => FakerData.faker.address.streetAddress(false))
  val randAddress2 = udf((value: String) => "")
  val randCity = udf((value: String) => FakerData.faker.address.city)
  val randState = udf((value: String) => FakerData.faker.address.stateAbbr)
  val randZip = udf((value: String) => FakerData.faker.address.zipCode)
  val rndMemberId = udf((value: String) => FakerData.faker.code.isbn13)
  val rndDOB = udf((value: String) => dob.filter(_.isDigit))
}

