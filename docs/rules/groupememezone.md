# Documentation `groupememezone`

## Definition

`groupememezone` est un type YAML non autonome.

Il ne doit pas etre utilise comme une regle simple importee seule dans `indexRules`.
Il est fait pour etre utilise dans le tableau `regles` d'une regle complexe importee via `indexComplexRules`.

Son role est de regrouper une ou plusieurs sous-regles qui doivent toutes s'appliquer a la meme occurrence d'une zone.

Autrement dit, `groupememezone` ne signifie pas seulement "des regles qui parlent de la meme zone 328".
Il signifie "des regles qui doivent etre vraies sur la meme 328".

## Ce que fait `groupememezone`

Quand le moteur evalue un bloc `groupememezone` :

1. il prend la zone declaree sur le bloc, par exemple `328` ;
2. il applique la premiere sous-regle a cette zone ;
3. il conserve les occurrences de la zone qui satisfont cette sous-regle ;
4. il applique ensuite les sous-regles suivantes sur cette meme liste d'occurrences ;
5. il fait une intersection des occurrences valides ;
6. le bloc est valide s'il reste au moins une occurrence de la zone qui satisfait toutes les sous-regles du groupe.

Le bloc permet donc d'exprimer :

- "dans une meme zone 328, la premiere sous-zone doit etre `$z`"
- "dans cette meme zone 328, la sous-zone `$z` doit contenir `Reproduction`"

Sans `groupememezone`, ces deux controles pourraient etre valides sur deux zones `328` differentes de la meme notice, ce qui ne couvrirait pas le besoin metier.

## Ou l'utiliser

`groupememezone` s'utilise a l'interieur de `regles:` dans une regle complexe.

Exemple de structure :

```yaml
rules:
  - id: 9300
    id-excel: 182
    message: "Exemple de regle complexe avec groupememezone"
    priorite: P1
    regles:
      - id: 9301
        type: presencechainecaracteres
        zone: "215"
        souszone: "a"
        type-de-verification: CONTIENT
        chaines-caracteres:
          - chaine-caracteres: "microfiche"

      - id: 9302
        type: groupememezone
        zone: "328"
        operateur-booleen: ET
        regles:
          - id: 9303
            type: positionsouszone
            souszone: "z"
            positions:
              - position: 1
                comparateur: DIFFERENT
```

Dans cet exemple :

- la regle complexe controle d'abord `215$a` ;
- puis elle enchaine avec un bloc `groupememezone` sur `328` ;
- ce bloc dit qu'une meme occurrence de `328` doit respecter les sous-regles internes.

## Types de regles autorises derriere `groupememezone`

Les sous-regles autorisees dans `groupememezone` sont uniquement :

- `presencezone`
- `presencesouszone`
- `positionsouszone`
- `presencechainecaracteres`
- `indicateur`

Les autres types de regles ne doivent pas etre places dans ce bloc.

En particulier, `groupememezone` n'est pas prevu pour :

- `dependance`
- `reciprocite`
- `comparaisondate`
- `comparaisoncontenusouszone`
- `nombrezone`
- `nombresouszone`
- `nombrecaractere`
- `typedocument`
- `typecaractere`
- `presencesouszonesmemezone`

## Contraintes de syntaxe

### 1. Le bloc doit porter une `zone`

Le bloc `groupememezone` doit toujours definir sa `zone`.

Exemple :

```yaml
- id: 9302
  type: groupememezone
  zone: "328"
  regles:
```

### 2. Les sous-regles internes ne doivent pas redefinir la `zone`

La `zone` est portee par le bloc `groupememezone`.
Les sous-regles internes heritent de cette zone.

Il ne faut donc pas faire ceci :

```yaml
- id: 9303
  type: positionsouszone
  zone: "328"
  souszone: "z"
```

Il faut faire ceci :

```yaml
- id: 9303
  type: positionsouszone
  souszone: "z"
```

### 3. Le bloc doit contenir au moins une sous-regle

`groupememezone` doit contenir au moins une sous-regle dans `regles:`.

### 4. Operateur de la premiere sous-regle du groupe

La premiere sous-regle a l'interieur de `groupememezone` ne doit pas porter `operateur-booleen`.

Exemple correct :

```yaml
regles:
  - id: 9303
    type: positionsouszone
    souszone: "z"
    positions:
      - position: 1
        comparateur: DIFFERENT
```

### 5. Operateur des sous-regles suivantes du groupe

Si le groupe contient une deuxieme sous-regle, une troisieme, etc., elles doivent porter `operateur-booleen`.

Exemple :

```yaml
regles:
  - id: 9303
    type: positionsouszone
    souszone: "z"
    positions:
      - position: 1
        comparateur: DIFFERENT

  - id: 9304
    type: presencechainecaracteres
    operateur-booleen: ET
    souszone: "z"
    type-de-verification: CONTIENT
    chaines-caracteres:
      - chaine-caracteres: "Reproduction"
```

## Attention aux deux niveaux d'operateurs

Il faut distinguer deux notions differentes.

### `operateur-booleen`

`operateur-booleen` sert a lier une regle ou un bloc a la regle precedente.

On le trouve :

- entre deux sous-regles d'une regle complexe ;
- entre deux sous-regles d'un bloc `groupememezone`.

Exemple :

```yaml
- id: 9302
  type: groupememezone
  zone: "328"
  operateur-booleen: ET
```

Ici, `operateur-booleen: ET` ne relie pas les sous-regles internes du groupe.
Il relie le bloc `groupememezone` a la regle precedente de la regle complexe.

### `operateur`

`operateur` est un attribut interne a certains types de regles, notamment `positionsouszone`.
Il sert a combiner plusieurs criteres internes de cette regle.

Exemple :

```yaml
- id: 9403
  type: positionsouszone
  souszone: "z"
  positions:
    - position: 1
      comparateur: DIFFERENT
    - position: 3
      comparateur: DIFFERENT
  operateur: OU
```

Ici, `operateur: OU` sert uniquement a combiner les deux entrees du tableau `positions`.

## Regle importante sur `positionsouszone`

Si une regle `positionsouszone` ne contient qu'une seule entree dans `positions`, il n'est pas necessaire d'ajouter `operateur`.

Exemple recommande :

```yaml
- id: 9303
  type: positionsouszone
  souszone: "z"
  positions:
    - position: 1
      comparateur: DIFFERENT
```

Exemple inutilement verbeux :

```yaml
- id: 9303
  type: positionsouszone
  souszone: "z"
  positions:
    - position: 1
      comparateur: DIFFERENT
  operateur: OU
```

Ce dernier exemple n'est pas utile car il n'y a qu'une seule position a combiner.

## Exemple YAML minimal avec une seule sous-regle dans le groupe

```yaml
rules:
  - id: 9200
    id-excel: 182
    message: "Si 215$a contient 'microfiche', alors la zone 328 doit commencer par une sous-zone $z"
    priorite: P1
    regles:
      - id: 9201
        type: presencechainecaracteres
        zone: "215"
        souszone: "a"
        type-de-verification: CONTIENT
        chaines-caracteres:
          - chaine-caracteres: "microfiche"

      - id: 9202
        type: groupememezone
        zone: "328"
        operateur-booleen: ET
        regles:
          - id: 9203
            type: positionsouszone
            souszone: "z"
            positions:
              - position: 1
                comparateur: DIFFERENT
```

Lecture de cet exemple :

- `9201` teste `215$a contient microfiche` ;
- `9202` ouvre un bloc `groupememezone` sur `328` ;
- `9203` verifie qu'une meme occurrence de `328` ne commence pas par autre chose que `$z`.

## Exemple YAML avec deux sous-regles dans le meme groupe

```yaml
rules:
  - id: 9300
    id-excel: 182
    message: "Si 215$a contient 'microfiche', alors une meme zone 328 doit commencer par $z et contenir 'Reproduction' dans $z"
    priorite: P1
    regles:
      - id: 9301
        type: presencechainecaracteres
        zone: "215"
        souszone: "a"
        type-de-verification: CONTIENT
        chaines-caracteres:
          - chaine-caracteres: "microfiche"

      - id: 9302
        type: groupememezone
        zone: "328"
        operateur-booleen: ET
        regles:
          - id: 9303
            type: positionsouszone
            souszone: "z"
            positions:
              - position: 1
                comparateur: DIFFERENT

          - id: 9304
            type: presencechainecaracteres
            operateur-booleen: ET
            souszone: "z"
            type-de-verification: CONTIENT
            chaines-caracteres:
              - chaine-caracteres: "Reproduction"
```

Lecture de cet exemple :

- `9303` controle la position de la sous-zone `$z` ;
- `9304` controle le contenu de cette sous-zone `$z` ;
- les deux controles doivent etre vrais sur la meme occurrence de `328`.

## Resume

- `groupememezone` est un type YAML non autonome ;
- il doit etre utilise dans une regle complexe ;
- il doit contenir au moins une sous-regle ;
- les sous-regles internes autorisees sont :
  - `presencezone`
  - `presencesouszone`
  - `positionsouszone`
  - `presencechainecaracteres`
  - `indicateur`
- la `zone` se declare sur le bloc `groupememezone`, pas dans les sous-regles internes ;
- la premiere sous-regle du groupe n'a pas `operateur-booleen` ;
- les suivantes doivent avoir `operateur-booleen` ;
- `operateur` dans `positionsouszone` ne sert que si la regle combine plusieurs positions.
