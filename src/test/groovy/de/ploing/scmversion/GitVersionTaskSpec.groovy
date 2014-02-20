package de.ploing.scmversion

import spock.lang.Specification

/**
 * @author Stefan Schlott
 */
class GitVersionTaskSpec extends Specification {
    def "Versions are sorted correctly"() {
        setup:
            def v1 = [1,3,5,2]
            def v2 = [1,3,6,0]
            def v3 = [1,4,1]
            def v4 = [1,4,1,0]
        when:
            def compV1V2 = SCMVersionPluginExtension.compareVersions(v1, v2)
            def compV2V1 = SCMVersionPluginExtension.compareVersions(v2, v1)
            def compV2V3 = SCMVersionPluginExtension.compareVersions(v2, v3)
            def compV3V2 = SCMVersionPluginExtension.compareVersions(v3, v2)
            def compV3V4 = SCMVersionPluginExtension.compareVersions(v3, v4)
        then:
            compV1V2 < 0
            compV2V1 > 0
            compV2V3 < 0
            compV3V2 > 0
            compV3V4 == 0
    }
}
