/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {

        //create an animation that rotates the ImageView containing the star from a value of -360 to 0.

        /**
         * Note:
         * The reason that the animation starts at -360 is that that allows the star to complete
         * a full circle (360 degrees) and end at 0, which is the default rotation value
         * for a non-rotated view, so it's a good value to have at the end of the animation
         * (in case any other action occurs on that view later, expecting the default value
         *
         */

        val animator = ObjectAnimator.ofFloat(star, View.ROTATION,-360f,0f)
        animator.duration = 1000
        animator.disableViewDuringAnimation(rotateButton)
        animator.start()
    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star,View.TRANSLATION_X,200f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,4f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(star,scaleX,scaleY)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star,View.ALPHA,0f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }


    @SuppressLint("ObjectAnimatorBinding")
    private fun colorizer() {
        val animator = ObjectAnimator.ofArgb(star.parent,
            "backgroundColor", Color.BLACK,Color.RED)

        animator.setDuration(500)
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.repeatCount = 1
        animator.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup

        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()

        /**
         * Create a new View to hold the star graphic. Because the star is a VectorDrawable asset, use an AppCompatImageView, which has the ability to host that kind of resource. Create the star and add it to the background container.
         */
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        container.addView(newStar)

        /**
         * Modify the star to have a random size, from .1x to 1.6x of its default size. Use this scale factor to change the cached width/height values, because you will need to know the actual pixel height/width for later calculations.
         */
        newStar.scaleX = Math.random().toFloat()* 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        /**
         * Now position the new star. Horizontally, it should appear randomly somewhere from the left edge to the right edge.
         */
        newStar.translationX = Math.random().toFloat() * containerW - starW/2

        /**
         * Create animators to for star rotation and falling
         */

        val mover = ObjectAnimator.ofFloat(newStar,View.TRANSLATION_Y,-starH,containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(newStar,View.ROTATION,
            (Math.random() * 1080).toFloat())

        rotator.interpolator = LinearInterpolator()

        /**
         * Run the animations in parallel with AnimatorSet
         */
        val set = AnimatorSet()
        set.playTogether(mover,rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        /**
         * Once newStar has fallen off the bottom of the screen, it should be removed from the container. Set a simple listener to wait for the end of the animation and remove it. Then start the animation
         */

        set.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                container.removeView(newStar)
            }
        })

        set.start()

    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                view.isEnabled = true
            }
        })
    }

}
