/*
 * Copyright (C) 2018 Wild Swift
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.wildswift.testapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import name.wildswift.mapache.config.GenerateNavigation
import name.wildswift.mapache.contextintegration.ActivityCaller
import name.wildswift.mapache.contextintegration.ActivityEventsCallback
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.TestAppNavigationStateMachine
import name.wildswift.testapp.generated.newNavigationStateMachine

@GenerateNavigation("TestApp")
class MainActivity : Activity(), ActivityCaller {
    private val stateMachine: TestAppNavigationStateMachine by lazy { newNavigationStateMachine(DiContext(applicationContext)) }

    private var callbacks = listOf<ActivityEventsCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateMachine.attachToActivity(this, this)
    }

    override fun onPause() {
        super.onPause()
        stateMachine.pause()
    }

    override fun onResume() {
        super.onResume()
        stateMachine.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        stateMachine.detachFromActivity()
    }

    override fun registerEventsCallback(callback: ActivityEventsCallback) {
        this.callbacks += callback
    }

    override fun removeEventsCallback(callback: ActivityEventsCallback) {
        this.callbacks = callbacks.filterNot { it == callback }
    }

    override fun onBackPressed() {
        this.callbacks.forEach { it.onBackPressed() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val result = permissions.mapIndexed {index, s -> s to grantResults[index] }.toMap()
        this.callbacks.forEach { it.onPermissionsResult(requestCode, result) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.callbacks.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }
}