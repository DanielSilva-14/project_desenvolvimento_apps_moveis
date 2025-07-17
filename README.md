# Projeto SummitHub: Plataforma de Eventos e Artigos

Este é o repositório do projeto final de Desenvolvimento de Aplicações Móveis, um sistema completo que consiste numa aplicação web (backend e API) e uma aplicação cliente para Android.

## Estrutura do Projeto

*   `/android-app/`: Código fonte da aplicação Android, desenvolvida em **Kotlin** com Android Studio.
*   `/backend-php/`: Código fonte do website e da API, desenvolvido em **PHP**.
*   `/database/`: Contém o script SQL (`summithub.sql`) para criar a estrutura da base de dados.
*   `/docs/`:  Relatório final.

## Como Executar

### 1. Backend (Servidor Web)
1.  Certifique-se que tem um ambiente de servidor local como XAMPP ou WAMP instalado.
2.  Crie uma nova base de dados no phpMyAdmin.
3.  Importe o ficheiro `database/summithub.sql` para a base de dados que criou.
4.  Copie o conteúdo da pasta `backend-php` para a pasta `htdocs` do seu XAMPP.
5.  **Atenção:** Verifique o ficheiro de ligação à base de dados e ajuste o nome de utilizador, senha e nome da base de dados se necessário.

### 2. Aplicação Android
1.  Abra a pasta `/android-app` no Android Studio.
2.  Deixe o Gradle sincronizar todas as dependências do projeto.
3.  No código da aplicação, encontre o local onde o endereço da API está definido e certifique-se que aponta para o seu servidor local.
4.  Compile e execute a aplicação num emulador ou dispositivo físico.
