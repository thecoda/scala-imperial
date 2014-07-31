package imperial



trait CheckResult {
  def isHealthy: Boolean
  def message: String
  def error: Option[Throwable]
}





