/*
 * Copyright 2014 the original author or authors.
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
package de.ploing.scmversion

import de.ploing.scmversion.task.SetVersionTask


/**
 * During initialization, the plugin will set the version attribute (which, conveniently, is of type Object)
 * to an instance of this class. If the project configuration accesses this instance, the toString method
 * will return the generated version string.<p>
 * Please beware that the plugin initialization must be done beforehand!
 *
 * @author Stefan Schlott
 */
class VersionProxy {
    SetVersionTask task

    VersionProxy(SetVersionTask task) {
        this.task = task
    }

    @Override
    String toString() {
        return task.getVersion()
    }
}
