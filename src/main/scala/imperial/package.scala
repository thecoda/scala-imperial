package imperial

import com.codahale.{metrics => ch}

object `package` {
  type Snapshot = ch.Snapshot
  type HealthCheck = ch.health.HealthCheck
}
