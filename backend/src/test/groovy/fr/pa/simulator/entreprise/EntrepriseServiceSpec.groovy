package fr.pa.simulator.entreprise

import spock.lang.Specification

class EntrepriseServiceSpec extends Specification {

    EntrepriseRepository repository = Mock()
    EntrepriseService service = new EntrepriseService(repository)

    def "enregistre une entreprise et renvoie une clé API en clair"() {
        given:
        def command = new RegisterEntrepriseCommand("552081317", "55208131766522", "ACME", "FR55552081317")

        when:
        def result = service.register(command)

        then:
        1 * repository.existsBySiret("55208131766522") >> false
        1 * repository.save(_ as Entreprise) >> { Entreprise e -> e }
        result.apiKey().startsWith("pa_")
        result.entreprise().siret() == "55208131766522"
        result.entreprise().siren() == "552081317"
    }

    def "rejette un SIRET déjà enregistré"() {
        given:
        repository.existsBySiret(_ as String) >> true

        when:
        service.register(new RegisterEntrepriseCommand("552081317", "55208131766522", "ACME", null))

        then:
        thrown(DuplicateSiretException)
    }

    def "rejette un SIREN incohérent avec le SIRET"() {
        when:
        service.register(new RegisterEntrepriseCommand("999999999", "55208131766522", "ACME", null))

        then:
        thrown(IllegalArgumentException)
    }

    def "authenticate renvoie vide quand la clé est absente"() {
        expect:
        service.authenticate(null).isEmpty()
        service.authenticate("   ").isEmpty()
    }
}
