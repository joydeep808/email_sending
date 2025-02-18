

# Setup 
    * You need to convert .env.dev to -> .env 
    * And you have to provide all the details
    
# Api
  * My application is runnuing on 3000
  * To create a email request hit this url 
      * POST  http://localhost:3000/api/v1/email
        ** { recipient: "" , body:"" , subject:""}
  * To get the status 
      * GET http://localhost:3000/api/v1/email?id=""

# IF you didnot provide the valid details this application will throw an error 

        