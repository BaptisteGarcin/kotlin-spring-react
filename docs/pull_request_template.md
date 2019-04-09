# Nouvelle pull request

**TODO** : Relire [Préparer une code review](https://github.com/altima-assurances/wiki/wiki/Code-review-et-Pull-Requests#pr%C3%A9parer-une-code-review)

**TODO** : Ajouter une description courte de la pull request expliquant les raisons des changements : corriger un bug, introduire une nouvelle fonctionnalité, améliorer le design ...

**TODO** : Définissez le type de changement

**TODO** : Vérifiez que le numéro de version à été modifié pour éviter les conflits : `X.Y.Z-{topic}-SNAPSHOT`

# Definition of done

## Build

- [ ] Est-ce que ça compile ? `./gradlew build`
- [ ] Est-ce que les tests exitants passent ?
- [ ] Est-ce que les modifications sont couvertes par des nouveaux tests ?
- [ ] Est-ce que le code modifié a été refactoré ?
- [ ] Le code a été buildé par l'intégration continue ?

## Documentation API

- [ ] La documentation utilisateur ApiDoc a-t-elle été produite ?
  - [ ] Technique - Endpoint ?
  - [ ] Fonctionnelle - Comment utiliser ?

Merci de respecter le [code de conduite](https://github.com/altima-assurances/wiki/wiki/Code-Review---Code-of-conduct) afin d'avoir des échanges positifs et bénéfiques :smile:
