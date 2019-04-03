// See LICENSE for license details.

package examples

import chiselTests.ChiselFlatSpec
import chisel3.testers.BasicTester
import chisel3._
import chisel3.core.{Bool, CompileOptions, WhenContext}
import chisel3.internal.sourceinfo.SourceInfo
import chisel3.util._


object spec {  // scalastyle:ignore object.name
  def apply(block: => Unit)(implicit sourceInfo: SourceInfo,
                            compileOptions: CompileOptions): SpecContext = {
    new SpecContext(sourceInfo, block)
  }
}

final class SpecContext(sourceInfo: SourceInfo, block: => Unit, firrtlDepth: Int = 0) {
  println("spec {")

  when(false.B) {
    block
  }

  println("}")

  def impl(block: => Unit)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Unit = {
    println("impl {")

    when(true.B) {
      block
    }

    println("}")
  }
}



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
  io.c := 0.U
  io.status := 0.U


  spec {
    switch(io.ctrl) {
      is(0.U) {
        io.c := io.a + io.b
      }
      is(1.U) {
        io.c := io.a - io.b
      }
    }
  } .impl {
    val is_add = io.ctrl === 0.U
    val is_sub = io.ctrl === 1.U
    val b = Mux(is_sub, ~io.b, io.b)
    when(is_add || is_sub) {
      io.c := io.a + b
    }
  }

}

class AnonymousModuleTester(mod: => TestClass) extends BasicTester {
  val dut = Module(mod)
  dut.io.a := 0.U
  dut.io.b := 0.U
  dut.io.ctrl := 0.U
}

class AnonymousModuleSpec extends ChiselFlatSpec {
  "An AnonymousModule" should "work" in {
    assertTesterPasses { new AnonymousModuleTester(new A) }
  }
}
