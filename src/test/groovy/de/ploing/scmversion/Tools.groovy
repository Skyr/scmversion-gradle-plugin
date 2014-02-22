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
