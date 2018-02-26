# Create the demo from scratch

1) Generate project from archetype with:
    
    > mvn archetype:generate 
                -DarchetypeGroupId=org.onehippo.cms7 
                -DarchetypeArtifactId=hippo-project-archetype 
                -DarchetypeVersion=major.0.x
                
    Fill in:               
    ```
       groupId: org.onehippo.forge.settingsmanagement.demo
       artifactId: settingsmanagementdemo
       version: major.0.0-SNAPSHOT
       package: org.onehippo.forge.settingsmanagement.demo
       projectName: Hippo Forge Settings Management Demo
    ```
 
2) Install the translations addon as documented 
   (or redo the dependency management as in existing demo's root, cms and site poms)
   
3) The repository-data/application HCM module should have `group: after: [hippo-cms, hippo-forge]`
