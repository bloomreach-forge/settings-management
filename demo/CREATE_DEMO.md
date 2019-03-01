# Create the demo from scratch

1) Generate project from archetype with:
    
    > mvn archetype:generate 
                -DarchetypeGroupId=org.onehippo.cms7 
                -DarchetypeArtifactId=hippo-project-archetype 
                -DarchetypeVersion=major.0.0
                
    Fill in:               
    ```
       groupId: org.bloomreach.forge.settingsmanagement.demo
       artifactId: settingsmanagementdemo
       version: major.0.0-SNAPSHOT
       package: org.bloomreach.forge.settingsmanagement.demo
       projectName: Bloomreach Forge Settings Management Demo
    ```
 
2) Install the translations addon as documented 
   (or redo the dependency management as in existing demo's root and cms-dependencies poms)
   
3) The repository-data/application HCM module should have `group: after: [hippo-cms, bloomreach-forge]`
