"""
Import sample data for classification engine
"""

import predictionio
import argparse

def import_events(client, file):
  f = open(file, 'r')
  count = 0
  print "Importing data..."
  for line in f:

    print count

    if count > 10000:
      break

    data = line.rstrip('\r\n').split(",")
    plan = data[10]

    attr = [[] for i in range(12)]
    attr[0] = data[0];
    attr[1] = data[1];
    attr[2] = data[2];
    attr[3] = data[3];
    attr[4] = data[4];
    attr[5] = data[5];
    attr[6] = data[6];
    attr[7] = data[7];
    attr[8] = data[8];
    attr[9] = data[9];
    attr[10] = data[11];
#    attr[10] = data[11];
#    print(attr[8]);


    client.create_event(
      event="$set",
      entity_type="training_point",
      entity_id=str(count), # use the count num as user ID
      properties= {
        "attr0" : str(attr[0]),
        "attr1" : str(attr[1]),
        "attr2" : str(attr[2]),
        "attr3" : str(attr[3]),
        "attr4" : str(attr[4]),
        "attr5" : str(attr[5]),
        "attr6" : str(attr[6]),
        "attr7" : str(attr[7]),
        "attr8" : str(attr[8]),
        "attr9" : str(attr[9]),
        "attr10":str(attr[10]),
 #       "attr10" : (attr[11]),
        "plan" : int(plan)
      }
    )
    count += 1
  f.close()
  print "%s events are imported." % count

if __name__ == '__main__':
  parser = argparse.ArgumentParser(
    description="Import sample data for classification engine")
  parser.add_argument('--access_key', default='invald_access_key')
  parser.add_argument('--url', default="http://localhost:7070")
  parser.add_argument('--file', default="./data/original.csv")

  args = parser.parse_args()
  print args

  client = predictionio.EventClient(
    access_key=args.access_key,
    url=args.url,
    threads=5,
    qsize=500)
  import_events(client, args.file)
