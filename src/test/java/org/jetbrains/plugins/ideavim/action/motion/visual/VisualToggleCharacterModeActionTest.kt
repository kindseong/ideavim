/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

@file:Suppress("RemoveCurlyBracesFromTemplate")

package org.jetbrains.plugins.ideavim.action.motion.visual

import com.maddyhome.idea.vim.state.mode.Mode
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.maddyhome.idea.vim.helper.VimBehaviorDiffers
import com.maddyhome.idea.vim.helper.vimSelectionStart
import org.jetbrains.plugins.ideavim.VimTestCase
import org.jetbrains.plugins.ideavim.rangeOf
import org.junit.jupiter.api.Test

class VisualToggleCharacterModeActionTest : VimTestCase() {
  @Test
  fun `test vim start after enter visual`() {
    val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByText(before)
    typeText("v")
    val startOffset = (before rangeOf "found").startOffset
    kotlin.test.assertEquals(startOffset, fixture.editor.caretModel.primaryCaret.vimSelectionStart)
  }

  @Test
  fun `test vim start after enter visual with motion`() {
    val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was settled on some sodden sand
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByText(before)
    typeText("vl")
    val startOffset = (before rangeOf "found").startOffset
    kotlin.test.assertEquals(startOffset, fixture.editor.caretModel.primaryCaret.vimSelectionStart)
  }

  @Test
  fun `test vim start after enter visual multicaret`() {
    val before = """
            A Discovery

            I ${c}found it in a legendary land
            all rocks and lavender and tufted grass,
            where it was ${c}settled on some sodden sand
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByText(before)
    typeText("vl")
    val startOffset1 = (before rangeOf "found").startOffset
    val startOffset2 = (before rangeOf "settled").startOffset
    kotlin.test.assertEquals(startOffset1, fixture.editor.caretModel.allCarets[0].vimSelectionStart)
    kotlin.test.assertEquals(startOffset2, fixture.editor.caretModel.allCarets[1].vimSelectionStart)
  }

  @Test
  fun `test vim start after enter visual multicaret with merge`() {
    val before = """
            A Discovery

            I found it in ${c}a ${c}legendary land
            all rocks and lavender and tufted grass,
            where it was ${c}settled on some sodden sand
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByText(before)
    typeText("v2e")
    val startOffset1 = (before rangeOf "legendary").startOffset
    val startOffset2 = (before rangeOf "settled").startOffset
    kotlin.test.assertEquals(startOffset1, fixture.editor.caretModel.allCarets[0].vimSelectionStart)
    kotlin.test.assertEquals(startOffset2, fixture.editor.caretModel.allCarets[1].vimSelectionStart)
  }

  @Test
  fun `test vim start after enter visual multicaret with merge to left`() {
    val before = """
            A Discovery

            I found it in ${c}a ${c}legendary land
            all rocks and lavender and tufted grass,
            where it was ${c}settled on some sodden sand
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByText(before)
    typeText("v2b")
    val startOffset1 = (before rangeOf "legendary").startOffset
    val startOffset2 = (before rangeOf "settled").startOffset
    kotlin.test.assertEquals(startOffset1, fixture.editor.caretModel.allCarets[0].vimSelectionStart)
    kotlin.test.assertEquals(startOffset2, fixture.editor.caretModel.allCarets[1].vimSelectionStart)
  }

  @Test
  fun `test enter visual with count`() {
    doTest(
      "1v",
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}${c}f${se}ound it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count multicaret`() {
    doTest(
      "1v",
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and ${c}lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}${c}f${se}ound it in a legendary land
                    all rocks and ${s}${c}l${se}avender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with five count`() {
    doTest(
      "5v",
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}foun${c}d${se} it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with 100 count`() {
    doTest(
      "100v",
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}found it in a legendary land${c}${se}
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count after visual operation`() {
    doTest(
      listOf("vedx", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}it i${c}n${se} a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count after visual operation multicaret`() {
    doTest(
      listOf("vedx", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and ${c}lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}it i${c}n${se} a legendary land
                    all rocks and ${s}and tuf${c}t${se}ed grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count after visual operation multiple time`() {
    doTest(
      listOf("vedx", "1v", "<ESC>bb", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}it i${c}n${se} a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with double count after visual operation`() {
    doTest(
      listOf("vedx", "2v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}it in a l${c}e${se}gendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with ten count after visual operation`() {
    doTest(
      listOf("vedx", "10v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}it in a legendary land${c}${se}
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with double count after visual operation multiline`() {
    doTest(
      listOf("vjld", "2v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    har${c}d${se} by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with ten count after visual operation multiline`() {
    doTest(
      listOf("vjld", "10v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    har${c}d${se} by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count after multiline visual operation`() {
    doTest(
      listOf("vjld", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}rocks and lavender and tufted grass,
                    whe${c}r${se}e it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @VimBehaviorDiffers(description = "Different caret postion")
  @Test
  fun `test enter visual with count with dollar motion`() {
    doTest(
      listOf("v\$dj", "1v"),
      """
                    A Discovery

                    I${c} found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I
                    ${s}all rocks and lavender and tufted grass,${c}${se}
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      // Correct vim behaviour:
      /*"""
                  A Discovery

                  Iall rocks and lavender and tufted grass,
                  w${s}here it was settled on some sodden sand${c}${se}
                  hard by the torrent of a mountain pass.
              """.trimIndent(),*/
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @VimBehaviorDiffers(description = "Different caret position")
  @Test
  fun `test enter visual with count with dollar motion and down movement`() {
    // expect to see switches v, $, d, v.
    doTest(
      listOf("v\$dj", "1v", "j"),
      """
                    A Discovery

                    I${c} found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand[long line]
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I
                    ${s}all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand[long line]${c}${se}
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      // Correct vim behaviour:
      /* """
                   A Discovery

                   I
                   all rocks and lavender and tufted grass,
                   w${s}here it was settled on some sodden sand[long line]
                   hard by the torrent of a mountain pass.${c}${se}
               """.trimIndent(),*/
      Mode.VISUAL(SelectionType.CHARACTER_WISE),
    )
  }

  @Test
  fun `test enter visual with count after line visual operation`() {
    doTest(
      listOf("Vd", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    ${s}${c}all rocks and lavender and tufted grass,
                    ${se}where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @Test
  fun `test enter visual with count after line visual operation to line end`() {
    doTest(
      listOf("V3jd3k", "1v"),
      """
                    A Discovery

                    I found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.

                    ${c}The features it combines mark it as new
                    to science: shape and shade -- the special tinge,
                    akin to moonlight, tempering its blue,
                    the dingy underside, the checquered fringe.
      """.trimIndent(),
      """
                    A Discovery

                    I found it in a legendary land
                    ${s}all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
                    ${c}${se}
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @VimBehaviorDiffers(description = "Different caret position")
  @Test
  fun `test enter visual with count after line visual operation multicaret`() {
    doTest(
      listOf("Vd", "1v"),
      """
                    A ${c}Discovery

                    I found it in a legendary land
                    all ${c}rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    ${s}${c}
                    ${se}I found it in a legendary land
                    ${s}${c}where it was settled on some sodden sand
                    ${se}hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @Test
  fun `test enter visual with double count after line visual operation`() {
    doTest(
      listOf("Vd", "2v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    ${s}all rocks and lavender and tufted grass,
                    ${c}where it was settled on some sodden sand
                    ${se}hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @Test
  fun `test enter visual with ten count after line visual operation`() {
    doTest(
      listOf("Vd", "10v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    ${s}all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    ${c}hard by the torrent of a mountain pass.${se}
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @VimBehaviorDiffers(description = "Different caret position")
  @Test
  fun `test enter visual with count after line visual operation with dollar motion`() {
    doTest(
      listOf("V\$d", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    ${s}${c}all rocks and lavender and tufted grass,
                    ${se}where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.LINE_WISE),
    )
  }

  @Test
  fun `test enter visual with count after block visual operation`() {
    doTest(
      listOf("<C-V>jld", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}u${c}n${se}d it in a legendary land
                    al${s}r${c}o${se}cks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.BLOCK_WISE),
    )
  }

  @Test
  fun `test enter visual with count after block visual operation multiple time`() {
    doTest(
      listOf("<C-V>jld", "1v", "<ESC>kh", "1v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}u${c}n${se}d it in a legendary land
                    al${s}r${c}o${se}cks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.BLOCK_WISE),
    )
  }

  @Test
  fun `test enter visual with double count after block visual operation`() {
    doTest(
      listOf("<C-V>jld", "2v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}und${c} ${se}it in a legendary land
                    al${s}roc${c}k${se}s and lavender and tufted grass,
                    wh${s}ere${c} ${se}it was settled on some sodden sand
                    ha${s}rd ${c}b${se}y the torrent of a mountain pass.
      """.trimIndent(),
      Mode.VISUAL(SelectionType.BLOCK_WISE),
    )
  }

  @Test
  fun `test enter visual with ten count after block visual operation`() {
    doTest(
      listOf("<C-V>jld", "20v"),
      """
                    A Discovery

                    I ${c}found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand[long line]
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I ${s}und it in a legendary lan${c}d${se}
                    al${s}rocks and lavender and tufted grass${c},${se}
                    wh${s}ere it was settled on some sodden sa${c}n${se}d[long line]
                    ha${s}rd by the torrent of a mountain pass.${c}${se}
      """.trimIndent(),
      Mode.VISUAL(SelectionType.BLOCK_WISE),
    )
  }

  @Test
  fun `test enter visual with dollar motion count after block visual operation`() {
    doTest(
      listOf("<C-V>j\$d2j", "1v"),
      """
                    A Discovery

                    I${c} found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand[long line]
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                    A Discovery

                    I
                    a
                    ${s}where it was settled on some sodden sand[long line${c}]${se}
                    ${s}hard by the torrent of a mountain pass.${c}${se}
      """.trimIndent(),
      // correct vim behaviour
      /*"""
                  A Discovery

                  I
                  a
                  w${s}here it was settled on some sodden sand[long line${c}]${se}
                  h${s}ard by the torrent of a mountain pass.${c}${se}
              """.trimIndent(),*/
      Mode.VISUAL(SelectionType.BLOCK_WISE),
    )
  }

  @Test
  fun `test selectmode option`() {
    configureByText(
      """
                    A Discovery

                    I${c} found it in a legendary land
                    all rocks and lavender and tufted grass,
                    where it was settled on some sodden sand[long line]
                    hard by the torrent of a mountain pass.
      """.trimIndent(),
    )
    enterCommand("set selectmode=cmd")
    typeText("v")
    assertState(Mode.SELECT(SelectionType.CHARACTER_WISE))
  }
}
