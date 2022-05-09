package net.twisterrob.android.view.color

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.results.RunResult
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import java.util.Random
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class FastMathTest {

	@Test
	fun atan2MatchesBuiltin() {
		(0..360).forEach { angle ->
			val r = 1
			val x = r * (cos(angle.toFloat()))
			val y = r * (sin(angle.toFloat()))
			val expected = atan2(y, x)

			val result = FastMath.Atan2Faster.atan2(y, x)

			assertEquals("atan2($y, $x) -> $expected", expected, result, 1e-3f)
		}
	}

	@Test
	fun atan2FasterThanBuiltin() {
		val results = runClass(TestBenchmark::class)
			.toList()
			.map { it.aggregatedResult.primaryResult }
		
		fun pickScore(name: String): Double =
			results.single { it.label == name }.score

		val fastScore = pickScore("fast")
		val javaScore = pickScore("java")
		val kotlinScore = pickScore("kotlin")
		
		assertThat(fastScore, lessThan(javaScore))
		assertThat(fastScore, lessThan(kotlinScore))
	}

	open class TestBenchmark {
		@State(Scope.Thread)
		open class BenchmarkState {

			var x: Float = 0f
			var y: Float = 0f

			@Setup(Level.Trial)
			fun initialize() {
				val rand = Random()
				x = rand.nextInt().toFloat()
				y = rand.nextInt().toFloat()
			}
		}

		@Benchmark
		fun fast(state: BenchmarkState, bh: Blackhole) {
			val result = FastMath.Atan2Faster.atan2(state.y, state.x)
			bh.consume(result)
		}

		@Suppress("RemoveRedundantQualifierName")
		@Benchmark
		fun kotlin(state: BenchmarkState, bh: Blackhole) {
			val result = kotlin.math.atan2(state.y, state.x)
			bh.consume(result)
		}

		@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog", "RemoveRedundantQualifierName")
		@Benchmark
		fun java(state: BenchmarkState, bh: Blackhole) {
			val result = java.lang.Math.atan2(state.y.toDouble(), state.x.toDouble()).toFloat()
			bh.consume(result)
		}
	}
}

fun runClass(benchmark: KClass<*>): Collection<RunResult> =
	OptionsBuilder()
		.include(clazz(benchmark))
		.mode(Mode.AverageTime)
		.timeUnit(TimeUnit.NANOSECONDS)
		.warmupTime(TimeValue.milliseconds(500))
		.warmupIterations(1)
		.measurementTime(TimeValue.milliseconds(500))
		.measurementIterations(2)
		.threads(1)
		.forks(1)
		.shouldFailOnError(true)
		.shouldDoGC(true)
		.build()
		.let { Runner(it) }
		.run()

private fun clazz(container: KClass<*>): String {
	val clazz = container.java.name.replace('$', '.')
	return Regex.escape("$clazz.") + ".*"
}

@Suppress("unused")
private fun method(container: KClass<*>, ref: KFunction<*>): String {
	val clazz = container.java.name.replace('$', '.')
	val method = ref.name
	return Regex.escape("$clazz.$method")
}
