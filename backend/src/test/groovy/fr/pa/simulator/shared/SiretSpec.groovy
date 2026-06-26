package fr.pa.simulator.shared

import spock.lang.Specification

class SiretSpec extends Specification {

    def "extrait le SIREN des 9 premiers chiffres du SIRET"() {
        expect:
        new Siret("55208131766522").siren() == new Siren("552081317")
    }

    def "rejette un SIRET invalide : #value"() {
        when:
        new Siret(value)

        then:
        thrown(IllegalArgumentException)

        where:
        value << [null, "", "123", "abcdefghijklmn", "5520813176652"]
    }

    def "rejette un SIREN invalide : #value"() {
        when:
        new Siren(value)

        then:
        thrown(IllegalArgumentException)

        where:
        value << [null, "", "12", "abcdefghi", "5520813176"]
    }
}
