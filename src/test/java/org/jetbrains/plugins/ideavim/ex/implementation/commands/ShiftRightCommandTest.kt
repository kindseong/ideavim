/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.jupiter.api.Test

/**
 * @author Alex Plate
 */
class ShiftRightCommandTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT, "Replace term codes issues")
  @Test
  fun `test simple right shift`() {
    val before = """        Lorem ipsum dolor sit amet,
                      |        ${c}consectetur adipiscing elit
                      |        Sed in orci mauris.
                      |        Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    configureByJavaText(before)

    typeText(commandToKeys(">"))

    val after = """        Lorem ipsum dolor sit amet,
                      |            ${c}consectetur adipiscing elit
                      |        Sed in orci mauris.
                      |        Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    assertState(after)
  }

  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT, "Replace term codes issues")
  @Test
  fun `test double right shift`() {
    val before = """        Lorem ipsum dolor sit amet,
                      |        ${c}consectetur adipiscing elit
                      |        Sed in orci mauris.
                      |        Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    configureByJavaText(before)

    typeText(commandToKeys(">>"))

    val after = """        Lorem ipsum dolor sit amet,
                      |                ${c}consectetur adipiscing elit
                      |        Sed in orci mauris.
                      |        Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    assertState(after)
  }

  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT, "Replace term codes issues")
  @Test
  fun `test range right shift`() {
    val before = """        Lorem ipsum dolor sit amet,
                      |        ${c}consectetur adipiscing elit
                      |        Sed in orci mauris.
                      |        Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    configureByJavaText(before)

    typeText(commandToKeys("3,4>"))

    val after = """        Lorem ipsum dolor sit amet,
                      |        consectetur adipiscing elit
                      |            ${c}Sed in orci mauris.
                      |            Cras id tellus in ex imperdiet egestas.
    """.trimMargin()
    assertState(after)
  }

  @Test
  fun `test multiple carets`() {
    val before = """    I found it in a legendary land
                      |${c}all rocks and lavender and tufted grass,
                      |    ${c}where it was settled on some sodden sand
                      |    hard by the$c torrent of a mountain pass.
    """.trimMargin()
    configureByJavaText(before)

    typeText(commandToKeys(">"))

    val after = """    I found it in a legendary land
                      |    ${c}all rocks and lavender and tufted grass,
                      |        ${c}where it was settled on some sodden sand
                      |        ${c}hard by the torrent of a mountain pass.
    """.trimMargin()
    assertState(after)
  }
}
