package imperial
package wrappers.codahale

import imperial.measures.TimerContext
import com.codahale.{metrics => ch}

class CodaHaleBackedTimerContext(val raw: ch.Timer.Context) extends TimerContext {
  def stop(): Long = raw.stop()
}
