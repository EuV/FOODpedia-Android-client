PREFIX gr: <http://purl.org/goodrelations/v1#>
PREFIX fp: <http://foodpedia.tk/ontology#>
PREFIX fo: <http://purl.org/foodontology#>

SELECT ?ean ?name ?description ?mass ?energy ?proteins ?fat ?carbohydrates ?ingredients SUBSTR(STR(?additive), 30) AS ?additive
WHERE {
    ?s gr:hasEAN_UCC-13 '%s'.
    {
        ?s gr:hasEAN_UCC-13 ?ean.
        OPTIONAL { ?s gr:name ?name. FILTER(langMatches(lang(?name), 'RU')) }
        OPTIONAL { ?s gr:description ?description. FILTER(langMatches(lang(?description), 'RU')) }
        OPTIONAL { ?s fp:netto_mass ?mass. FILTER(langMatches(lang(?mass), 'RU')) }
        OPTIONAL { ?s fo:ingredientsListAsText ?ingredients. FILTER(langMatches(lang(?ingredients), 'RU')) }
        OPTIONAL { ?s fo:energyPer100gAsDouble ?energy. }
        OPTIONAL { ?s fo:proteinsPer100gAsDouble ?proteins. }
        OPTIONAL { ?s fo:fatPer100gAsDouble ?fat. }
        OPTIONAL { ?s fo:carbohydratesPer100gAsDouble ?carbohydrates. }
    } UNION {
        ?s fo:containsIngredient ?additive.
        ?additive rdf:type fo:E-additive.
    }
}
LIMIT 100
