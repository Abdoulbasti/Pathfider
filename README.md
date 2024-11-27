# Projet de GLA

Version 2024

## Lien vers la vidéo
https://youtu.be/eBVGWTBQfHg 

## Description

Ceci est l'archétype de projet de Génie Logiciel Avancé (GLA).

Il s'agit d'un projet Java. Ce dépôt définit un système de build et une application simple. Il est nécessaire de consulter le fichier [CONTRIBUTING.md](CONTRIBUTING.md) pour utiliser ce dépôt.

## Lancement du programme

Ce projet utilise [maven](https://maven.apache.org/) d'Apache pour la gestion de construction.

Afin de compiler et lancer les tests, exécutez simplement

```bash
mvn verify
```

Dans sa version initiale, le programme fournit est un simple code qui se lance en terminal ou en application graphique.

Une fois le programme compilé, vous trouverez un jar executable dans le dossier target. Au nom de jar près (version changeante), vous pourrez l'exécuter avec:

```
java -jar target/project-2024.1.0.0-SNAPSHOT.jar
```

L'option de lancement `--info` causera l'affichage dans la console d'informations de l'application.

L'option de lancement `--gui` causera l'ouverture d'une fenêtre affichant le logo de l'Université de Paris.

Sans option, le programme Pathfinder sera lancé.

## Tests JaCoCo

Afin de vérifier la couverture des tests via JaCoCo:

```bash
mvn clean jacoco:prepare-agent install jacoco:report
```

Les résultats seront stockés dans `target/site/jacoco/index.html`.  

Par la suite, ```mvn jacoco:report``` suffit.
