import org.apache.log4j.Logger
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import java.text.SimpleDateFormat
import java.util.Calendar

trait UserDatabase {

  val logger: Logger = Logger.getLogger(this.getClass)

  val userAccountMap = mutable.Map("Nimit" -> UserAccount(123,"Nimit", "Saharanpur", "nj11", 8800),
    "neha" -> UserAccount(124,"Abhay", "Delhi", "abh123", 8000))

  def addUserAccount(userName: String, customerAccount: UserAccount): Unit = {

    userAccountMap += (userName -> customerAccount)

  }

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate: String = dateFormat.format(Calendar.getInstance().getTime)

  private val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.phone, "PhoneBiller", 1L, currentDate, 100.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, currentDate, 200.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 300.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, currentDate, 400.00, 0, 0, 0.00)
    )
  )


  def getUserAccountMap: mutable.Map[String, UserAccount] = userAccountMap

  def getLinkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = linkedBiller


  def linkBiller(accountNo: Long, billerName: String, billerCategory: Category.Value): Boolean = {

    val listOfBillers = linkedBiller.getOrElse(accountNo, Nil)
    val linkedBillerCaseClass = LinkedBiller(accountNo, billerName, billerCategory)

    listOfBillers match {

      case listOfBillers: ListBuffer[LinkedBiller] if listOfBillers.nonEmpty =>
        linkedBiller(accountNo) += linkedBillerCaseClass
        true

      case Nil => linkedBiller += accountNo -> ListBuffer(linkedBillerCaseClass)
        true

    }

  }

  def depositSalary(accountNo: Long, customerName: String, salary: Double): Boolean = {
    logger.info("Depositing salary of " + salary)

    val customerAccountList = userAccountMap.values.filter(_.accountNo == accountNo)

    if (customerAccountList.isEmpty) {
      false
    }

    else {
      val customerAccount = customerAccountList.head
      val inititalBalance = customerAccount.initialAmount
      val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount + salary)
      userAccountMap(customerAccount.userName) = newCustomerAccount
      logger.info("Currently database is as: " + userAccountMap)
      true
    }

  }

  def payBill(accountNo: Long, billerCategory: Category.Value): Boolean = {

    val billToPayList = linkedBiller.getOrElse(accountNo, Nil).filter(_.billerCategory == billerCategory)
    if (billToPayList.isEmpty) {
      false
    }
    else {
      val billToPay = billToPayList.head.amount
      val initialAmountList = userAccountMap.values.filter(_.accountNo == accountNo)
      val initialAmount = initialAmountList.map(_.initialAmount).toList
      logger.info("Amount in the account is " + initialAmount.head)
      if (initialAmount.head > billToPay) {
        logger.info("If condition satisfied in payBill")
        val linkedBillerCaseClass = linkedBiller(accountNo).filter(_.billerCategory == billerCategory).head
        val dateWhilePayingBill = dateFormat.format(Calendar.getInstance().getTime)
        val newlinkedBillerCaseClass = linkedBillerCaseClass.copy(transactionDate = dateWhilePayingBill,
          amount = billToPay, totalIterations = linkedBillerCaseClass.totalIterations + 1,
          executedIterations = linkedBillerCaseClass.executedIterations + 1, paidAmount = linkedBillerCaseClass.amount + billToPay
        )

        val listOfLinkedBiller = linkedBiller(accountNo)
        listOfLinkedBiller -= linkedBillerCaseClass
        listOfLinkedBiller += newlinkedBillerCaseClass
        linkedBiller(accountNo) = listOfLinkedBiller

        logger.info("LinkedBiller map is as: " + linkedBiller)

        userAccountMap foreach {
          case (username, customerAccount) =>
            if (customerAccount.accountNo == accountNo) {
              val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
              userAccountMap(username) = newCustomerAccount
            }
            else {
              userAccountMap(username) = customerAccount
            }
        }
        logger.info("Returning true")
        true
      }
      else {
        logger.info("Returning false")
        false
      }
    }
  }
}