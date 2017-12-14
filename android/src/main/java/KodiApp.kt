import android.app.Application
import Kodi

/**
 * Created from milespeele on 7/3/15.
 */
abstract class KodiApp : Application() {

    lateinit var kodi : Kodi

    override fun onCreate() {
        super.onCreate()

        kodi = createRootKodi()
    }

    abstract fun createRootKodi() : Kodi
}
