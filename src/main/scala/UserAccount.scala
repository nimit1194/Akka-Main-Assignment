case class UserAccount(accountNo: Long, customerName: String,
                           address: String, userName: String, initialAmount: Double)


object UserAccount {

  def apply(infoList: List[String]): UserAccount = {

    val ONE = 1
    val TWO = 2
    val THREE = 3
    val FOUR = 4

    UserAccount(infoList.head.toLong, infoList(ONE), infoList(TWO),
      infoList(THREE), infoList(FOUR).toDouble)
  }

}