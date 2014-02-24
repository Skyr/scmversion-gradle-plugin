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

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author Stefan Schlott
 */
class Tools {
    static def extractZip(InputStream input, File destDir) {
        byte[] buffer = new byte[1024]
        ZipInputStream zis = new ZipInputStream(input);
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null) {
            String fileName = ze.getName()
            File newFile = new File(destDir, fileName)
            if (ze.isDirectory()) {
                newFile.mkdirs()
            } else {
                new File(newFile.getParent()).mkdirs()
                FileOutputStream fos = new FileOutputStream(newFile)
                int len
                while ((len=zis.read(buffer))>0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
            }
            zis.closeEntry()
            ze = zis.getNextEntry()
        }
        //close last ZipEntry
        zis.closeEntry()
        zis.close()
        input.close()
    }
}
