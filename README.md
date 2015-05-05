
# PredictionIOLinReg_JobSalary


## Overview
In this Job-Prediction template, we use MLLib's Linear Regression algorithm, on the publicly available data set for job salaries avaliable in kaggle

## Linear Regression
In statistics, linear regression is an approach for modeling the relationship between a scalar dependent variable and one or more explanatory variables (or independent variables). Linear Regression is widely used in practice, to learn real-valued outputs. The linear regression model of this template is trained using "Stochastic Gradient Descent", an on-line version of gradient descent where the true gradient is approximated by the gradient at a single training point. In this template, we employ Linear Regression to solve the prediction of job salaries based on relevant features.

## Usage

### Event Data Requirements
By default, the template requires the following events to be collected ( we can check this at TemplateFolder/data/import_eventserver.py ):

- user $set event, which set the attributes of the user

### Input Query
- array of features values ( 6 features)
```
{"features": ["ID", "Job Title", "Descriptio of job", "Employing Company", "Location", "part_time/full_time/(NA)", "contract/permanent/(NA)" ]}
```

### Output Predicted Result
- the predicted label 
```
{"prediction": Predicted Annual Income}
```

### Dataset 
We will be using the dataset that is available here [Job dataset ](https://www.kaggle.com/c/job-salary-prediction/data)
Download Train_rev1 and save it in the data folder that is present in the prediction template under the name 'original.csv'



## Install and Run PredictionIO
First you need to [install PredictionIO 0.9.1](http://docs.prediction.io/install/) (if you haven't done it).
Let's say you have installed PredictionIO at /home/yourname/PredictionIO/. For convenience, add PredictionIO's binary command path to your PATH, i.e. /home/yourname/PredictionIO/bin
```
$ PATH=$PATH:/home/yourname/PredictionIO/bin; export PATH
```
Once you have completed the installation process, please make sure all the components (PredictionIO Event Server, Elasticsearch, and HBase) are up and running.

```
$ pio-start-all
```
For versions before 0.9.1, you need to individually get the PredictionIO Event Server, Elasticsearch, and HBase up and running.

You can check the status by running:
```
$ pio status
```
## Create a new Engine Template
Clone the current repository by executing the following command in the directory where you want the code to reside:
    
```
git clone https://github.com/RAditi/PredictionIOLinReg_JobSalary.git
cd PredictionIOLinReg_JobSalary
```
## Generate an App ID and Access Key
Let's assume you want to use this engine in an application named "MyApp1". You will need to collect some training data for machine learning modeling. You can generate an App ID and Access Key that represent "MyApp1" on the Event Server easily:
```
$ pio app new MyApp1

```
You should find the following in the console output:
```
...
[INFO] [App$] Initialized Event Store for this app ID: 1.
[INFO] [App$] Created new app:
[INFO] [App$]       Name: MyApp1
[INFO] [App$]         ID: 1
[INFO] [App$] Access Key: 3mZWDzci2D5YsqAnqNnXH9SB6Rg3dsTBs8iHkK6X2i54IQsIZI1eEeQQyMfs7b3F
```
Take note of the Access Key and App ID. You will need the Access Key to refer to "MyApp1" when you collect data. At the same time, you will use App ID to refer to "MyApp1" in engine code.

$ pio app list will return a list of names and IDs of apps created in the Event Server.

```
$ pio app list
[INFO] [App$]                 Name |   ID |                                                       Access Key | Allowed Event(s)
[INFO] [App$]               MyApp1 |    1 | 3mZWDzci2D5YsqAnqNnXH9SB6Rg3dsTBs8iHkK6X2i54IQsIZI1eEeQQyMfs7b3F | (all)
[INFO] [App$]               MyApp2 |    2 | io5lz6Eg4m3Xe4JZTBFE13GMAf1dhFl6ZteuJfrO84XpdOz9wRCrDU44EUaYuXq5 | (all)
[INFO] [App$] Finished listing 2 app(s).
```

## Collecting Data


You can send these data to PredictionIO Event Server in real-time easily by making a HTTP request or through the EventClient of an SDK.

A Python import script import_eventserver.py is provided in the template to import the data to Event Server using Python SDK.
Replace the value of access_key parameter by your Access Key and run:
```python
$ cd MyRecomendation
$ python data/import_eventserver.py --access_key 3mZWDzci2D5YsqAnqNnXH9SB6Rg3dsTBs8iHkK6X2i54IQsIZI1eEeQQyMfs7b3F
```
You should see the following output:
```
Importing data...
100001 events are imported.
```
This python script converts the data file to proper events formats as needed by the event server.
Now the training data (the record of the clinical data downloaded above)is stored as events inside the Event Store.

## Deploy the Engine as a Service
Now you can build, train, and deploy the engine. First, make sure you are under the PredictionIO-MLLib-LinReg-Template.

### Engine.json

Under the directory, you should find an engine.json file; this is where you specify parameters for the engine.
Make sure the appId defined in the file match your App ID. (This links the template engine with the App)

Parameters for the Linear Regression model are to be set here. 
If the "intercept" parameter is set to 1, then the linear regression model is trained allowing for an intercept/bias (the constant term in the expression of the target variable as a linear combination of the explanatory variables). If the intercept parameter is 0, the bias term of the linear model is set to 0. 


```
{
  "id": "default",
  "description": "Default settings",
  "engineFactory": "org.template.classification.ClassificationEngine",
  "datasource": {
    "params": {
      "appId": 1
    }
  },
  "algorithms": [
    {
      "name": "algo",
      "params": {
        
        "intercept": 1,     
      }
    }
  ]
}
```
### Build

Start with building your PredictionIOLinReg_JobSalary engine.
```
$ pio build
```
This command should take few minutes for the first time; all subsequent builds should be less than a minute. You can also run it with --verbose to see all log messages.

Upon successful build, you should see a console message similar to the following.
```
[INFO] [Console$] Your engine is ready for training.
```

### Training the Predictive Model

Train your engine.

```
$ pio train
```
When your engine is trained successfully, you should see a console message similar to the following.

```
[INFO] [CoreWorkflow$] Training completed successfully.
```
### Deploying the Engine

Now your engine is ready to deploy.

```
$ pio deploy
```
This will deploy an engine that binds to http://localhost:8000. You can visit that page in your web browser to check its status.

## Use the Engine
As an example you can send this JSON { "features": ["1132243", "Engineering Systems Analyst", "CLient provides specialist software developement", "Gregory Martin International", "Dorking", "part_time", "contract" ] } to the deployed engine and it will return a JSON of the predicted salary. Simply send a query by making a HTTP request or through the EngineClient of an SDK:
where 
"1132243" : The ID
"Engineering Systems Analyst" : Post
"CLient provides specialist software developement" : Job Description
"Gregory Martin International" : Company
"Dorking" : Place of Employment
"part_time" : Type of job
"contract" : Type of contract

```python
import predictionio
engine_client = predictionio.EngineClient(url="http://localhost:8000")
print engine_client.send_query({"features" :["1132243", "Engineering Systems Analyst", "CLient provides specialist software developement", "Gregory Martin International", "Dorking", "part_time", "contract" ]})

```
The following is sample JSON response:

```
{"prediction" :17425.9983 }
```

The sample query can be found in **sample.py**, which can be executed :

```
python sample.py
```

## Feature Extraction
Currently, the template uses a very simple feature extraction technique. All the possible unique values of place, job type and contract type, are extracted from the entire training data. They are then used to encode a 1-hot set of features, with a vector for each possible value of the categorical variable and then setting the category of the data point to 1, in the feature corresponding to the value of that data point.
Further, the regression is performed on the logarithm of the salary values, because from the visualisation of the data structures, linear regression would work better on the logarithm of the values, instead of the original values. 


