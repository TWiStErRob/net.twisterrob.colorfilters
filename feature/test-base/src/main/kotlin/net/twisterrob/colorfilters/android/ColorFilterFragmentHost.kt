package net.twisterrob.colorfilters.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.mock
import net.twisterrob.colorfilters.android.testbase.R
import org.mockito.Answers
import kotlin.reflect.KClass

/**
 * Includes some Magic ported from TestPackageIntentRule and TestActivityCompat in Inventory.
 */
class ColorFilterFragmentHost(
	val listener: ColorFilterFragment.Listener = mock(defaultAnswer = Answers.RETURNS_DEEP_STUBS)
) :
	AppCompatActivity(),
	ColorFilterFragment.Listener by listener {

	override fun attachBaseContext(ignored: Context) {
		super.attachBaseContext(InstrumentationRegistry.getInstrumentation().targetContext)
	}

	override fun setContentView(@LayoutRes layoutResID: Int) {
		val inflater = LayoutInflater.from(InstrumentationRegistry.getInstrumentation().context)
		val view = inflater.inflate(layoutResID, null, false)
		super.setContentView(view)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(applicationInfo.theme)
		super.onCreate(savedInstanceState)

		setContentView(R.layout.fragment_host_activity)

		if (savedInstanceState == null) {
			val fragment = supportFragmentManager
				.fragmentFactory
				.instantiate(classLoader, intent.getStringExtra(EXTRA_FRAGMENT)!!)

			supportFragmentManager
				.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit()
		}
	}

	override fun onStart() {
		super.onStart()
		title = supportFragmentManager.findFragmentById(R.id.fragment_container)!!::class.simpleName
	}

	companion object {

		const val EXTRA_FRAGMENT = "fragment"

		fun <T : ColorFilterFragment> rule(clazz: KClass<T>): ActivityTestRule<ColorFilterFragmentHost> {
			return object : ActivityTestRule<ColorFilterFragmentHost>(ColorFilterFragmentHost::class.java) {
				/**
				 * Magical reference to the intent launching the activity in order to launch activity in test package.
				 */
				val intent = Intent()

				override fun getActivityIntent() = intent
					.putExtra(EXTRA_FRAGMENT, clazz.qualifiedName)

				override fun beforeActivityLaunched() {
					// set the package and class properly
					// AFTER ActivityTestRule has done its forceful usage of getTargetContext()
					intent.component?.let { component ->
						intent.setClassName(InstrumentationRegistry.getInstrumentation().context, component.className)
					}
				}
			}
		}
	}
}
