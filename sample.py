import predictionio
engine_client = predictionio.EngineClient(url = "http://localhost:8000")
print engine_client.send_query({"features" :["1132243", "Engineering Systems Analyst", "CLient provides specialist software developement", "Gregory Martin International", "Dorking", "part_time", "contract" ]})

