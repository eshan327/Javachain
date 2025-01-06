## About The Project

I've developed a proof of work implementation of a blockchain, utilizing Java and Maven.

Some key features include, but aren't limited to:
- Securely storing data through a chain of blocks.
- Utilizing cryptographic signatures to reinforce transaction security.
- A mining mechanism to validate and add new blocks.
- Validity checks.
- Wallet functionality with both public and private keys.
- The creation, processing, and transfer of transactions.

## Requirements
Make sure you:
1. Have an up-to-date [JDK](https://www.oracle.com/java/technologies/downloads/) (I use JDK 23)
2. Download [Maven](https://maven.apache.org/download.cgi), preferrably the latest version (3.9.9 currently)

This project also relies on 2 dependencies: Bouncy Castle for cryptography and Gson for JSON serialization. 
Both of these are added through Maven and can be observed in the pom.xml file, as is standard.

## Installation

Cloning the repo:
   ```sh
  git clone https://github.com/eshan327/Blockchain.git
  cd Blockchain
   ```
Building the project:
   ```sh
  mvn clean install
   ```
Running it with Maven:
   ```sh
  mvn exec:java -Dexec.mainClass="com.blockchain.Javachain"
   ```

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/eshan327/Blockchain/blob/main/LICENSE) file for details.
