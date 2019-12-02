# DFaaSCloud

Distributed Function-as-a-Service Simulator

## How to Install DFaaSCloud on Ubuntu

### 1. Install java 11

```
sudo add-apt-repository ppa:linuxuprising/java
sudo apt-get update
sudo apt-get install oracle-java11-installer
```
> [NOTE]
> Oracle Java 11 can’t be directly downloaded from Oracle website any more! Now you HAVE to log in and manually download
### 2. Download DFaaSCloud source

```
git clone https://github.com/etri/DFaaSCloud.git
```

### 3. Install IntelliJ IDEA
* Download Install IntelliJ IDEA community version from https://www.jetbrains.com/idea/
* Follow the instructions at https://www.jetbrains.com/help/idea/install-and-set-up-product.html

### 4. Setting DFaaSCloud project with IntelliJ IDEA

#### 4.1 Create a new IntelliJ project
* Create a project from existing sources
    * From the main menu, select `File | New | Project from Existing Sources`.
* For Project SDK, select version 11.

#### 4.2 Configure the project structure
* In Project tool window, select file pom.xml and click right mouse button, and select `Add as Maven Project`.
* In Project tool window, select file pom.xml and click right mouse button, and select `Maven | Generate Sources and Update Folders`.
* Open Project Structure window (by selecting `File | Project Structure`)
    * In `Modules` tab, select `resources` folder and mark it as Sources. 
    * In `Libraries` tab, select `jars`, and remove guava-18.0.jar. **THIS IS IMPORTANT!**
    * In `SDKs` tab, select version 11.

#### 4.3 Build and run project
* Select `Build | Build Project`.
* Select `Run | Edit Configurations`  and then `+` to create a run configuration and fill in the blanks as follows:
    * Type: Application
    * Name: DFaaSGui
    * Main class: org.faas.gui.DFaaSGui
    * Use classpath of module: DFaaSCloud
* Select `Run | Run DFaaSGui`.
 
### 5. Create an artifact (jar file) with IntelliJ IDEA
* Open Project Structure window (by selecting `File | Project Structure`)
    * After choosing `Artifacts` tab, select `+` and then `JAR | From modules with dependencies`. 
    * In the popped-up window, 
        * Choose `DFaaSCloud` module as Module
        * Select a class as Main Class (e.g., org.faas.gui.DFaaSGui or org.faas.gui.DFaaSMain).
        * Press 'OK'
    * For the artifact name,
        * If you use `org.faas.gui.DFaaSMain` as the main class, it is recommended to use the artifact name as `dfaascloud:jar`.
        * If you use `org.faas.gui.DFaaSGui` as the main class, it is recommended to use the artifact name to be `dfaascloud_gui:jar`.
    * Press 'OK'
> [NOTE] 
> At this moment, it is not easy to define two different artifacts from the same module. 
> Therefore, you should modify the artifact configuration to get a different version of DFaaSCloud.
    

* Select `Build | Build Artifacts`. 
    * Then, the artifact file should be generated under `out/artifacts/dfaascloud_jar/` or `out/artifacts/dfaascloud_gui_jar/`.

### 6. Run DFaaSCloud jar file at the terminal command line.

* For terminal version, run DFaaSCloud as follows.
```
cp out/artifacts/dfaascloud_jar/DFaaSCloud.jar dfaascloud.jar # copy the jar to the directory for running dfaascloud
java -jar dfaascloud.jar config/network_topology.json
```

* For GUI version, run DFaaSCloud as follows. 
    * When you copy the jar, it is recommended to rename the jar file to be `dfaascloud_gui.jar`.

```
cp out/artifacts/dfaascloud_gui_jar/DFaaSCloud.jar dfaascloud_gui.jar # copy the jar to the directory for running dfaascloud
java -jar dfaascloud_gui.jar config/network_topology.json
```
