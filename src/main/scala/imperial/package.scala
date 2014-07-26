package imperial

import com.codahale.{metrics => ch}

object `package` {
  type Snapshot = ch.Snapshot
  implicit def enrichCounter(c: ch.Counter): Counter = Counter(c)

}
