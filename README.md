# tp3
## Tests des endpoints avec Postman et cURL
« Test de l'endpoint POST /api/categories sous Postman, validant l'ajout d'une catégorie (201 Created). »
<img width="569" height="395" alt="14tp3screenpost categ" src="https://github.com/user-attachments/assets/b7193135-d094-4590-b7da-bbba1484f87c" />

« Test de l'endpoint POST /api/products sous Postman, validant l'ajout d'un produit avec le statut 201 Created. »
<img width="579" height="458" alt="posttp3postproductpage14" src="https://github.com/user-attachments/assets/63382a4f-cc33-4d8d-95fd-1a51068c3b7a" />

« Test de l'endpoint GET /api/products/1 sous Postman, validant la récupération d'un produit par son ID (200 OK). »
<img width="522" height="400" alt="getpage14 apiproduct byid" src="https://github.com/user-attachments/assets/644b5e14-b74e-4f44-8c9c-32412f551683" />

« Test de l'endpoint DELETE /api/products/1 sous Postman, validant la suppression d'un produit avec le statut 204 No Content. »
<img width="554" height="374" alt="deletebyidpage14" src="https://github.com/user-attachments/assets/af076c26-1a03-4aad-bed7-85c600937347" />

« Test de l'endpoint POST /api/products via PowerShell (Invoke-RestMethod), validant l'ajout d'un produit avec succès. »
<img width="584" height="224" alt="postpowersghell" src="https://github.com/user-attachments/assets/9e754a78-bca2-479b-8369-e079ca7ab719" />

« Vérification de l'enregistrement effectif du produit dans la base de données via une requête SQL SELECT * FROM products; (phpMyAdmin). »

<img width="542" height="368" alt="verificationdatabase" src="https://github.com/user-attachments/assets/b7076fe6-9576-4035-9ccc-c86f5c14047b" />

« Test de l'endpoint GET /api/products/2 sous Postman, validant la récupération réussie du produit créé (200 OK). »

<img width="589" height="428" alt="lastcheck" src="https://github.com/user-attachments/assets/a5139084-a110-49c9-80f9-83be868f9a23" />


## Exercice 3 – Gestion de la révocation des JWT

### 1. Problématique

Un JWT est un token autonome : une fois généré, il reste valide jusqu'à son expiration, même si l'utilisateur souhaite se déconnecter ou si le token est compromis.

Pour réduire ce risque, plusieurs stratégies peuvent être utilisées.

### 2. Stratégies proposées

#### A. Liste noire en base de données (Blacklist)

- Lorsqu'un utilisateur se déconnecte ou signale un token compromis, son identifiant unique (`jti`) est ajouté à une table `revoked_tokens`.
- À chaque requête, le filtre JWT vérifie si le token figure dans cette liste.
- Si le token est révoqué, l'accès est refusé avec une réponse HTTP 401.
- Les entrées peuvent être supprimées automatiquement après l'expiration des tokens.

**Avantage :** permet de révoquer immédiatement un token précis.

**Inconvénient :** nécessite une vérification en base de données à chaque authentification.

#### B. Durée de vie courte

- Réduire la durée de validité des access tokens (par exemple, 10 à 15 minutes).
- Utiliser un mécanisme de rafraîchissement pour obtenir un nouveau token.
- En cas de vol, le token compromis ne sera utilisable que pendant une courte période.

**Avantage :** limite les conséquences d'un vol de token.

**Inconvénient :** ne permet pas de révoquer immédiatement un token encore valide.

#### C. Rotation du secret JWT

- Si la clé secrète est compromise, générer une nouvelle clé sécurisée.
- Les tokens signés avec l'ancienne clé deviennent invalides.
- Tous les utilisateurs doivent alors se reconnecter pour obtenir de nouveaux tokens.

**Avantage :** permet d'invalider tous les tokens signés avec l'ancienne clé.

**Inconvénient :** oblige tous les utilisateurs à se reconnecter.

### 3. Solution recommandée

Pour cette application Spring Boot, une combinaison de ces stratégies est recommandée :

1. Utiliser des access tokens de courte durée.
2. Mettre en place une liste noire pour révoquer individuellement les tokens.
3. Effectuer une rotation du secret en cas de compromission de la clé.

### 4. Conclusion

La combinaison de ces mécanismes améliore la sécurité de l'authentification JWT. Elle permet de limiter la durée d'utilisation d'un token volé, de révoquer un token individuellement et d'invalider tous les tokens en cas de compromission du secret.

