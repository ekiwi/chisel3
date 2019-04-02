// See LICENSE for license details.

package examples

import chiselTests.ChiselFlatSpec
import chisel3.testers.BasicTester
import chisel3._
import chisel3.util._


abstract class TestClass extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
    val c = Output(UInt(8.W))
    val ctrl = Input(UInt(3.W))
    val status = Output(UInt(2.W))
  })
}

class A extends  TestClass {
  io.status := 0.U

}

// Accept a reference to a SimpleVendingMachine so it can be constructed inside
// the tester (in a call to Module.apply as required by Chisel
class AnonymousModuleTester(mod: => TestClass) extends BasicTester {
  val dut = Module(mod)
}

class AnonymousModuleSpec extends ChiselFlatSpec {
  "An AnonymousModule" should "work" in {
    assertTesterPasses { new AnonymousModuleTester(new A) }
  }
}
