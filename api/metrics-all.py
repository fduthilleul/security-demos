import os
import re
import json
from prometheus_client import start_http_server, Metric, REGISTRY
import time


class MyCollector(object):
    def __init__(self, json_dir):
        self.json_dir = json_dir

    def collect(self):
        for filename in os.listdir(self.json_dir):
            if filename.endswith('.json'):
                json_path = os.path.join(self.json_dir, filename)
                metric = Metric(f'stackrox_{sanitize_metric_name(filename)}', f'My custom metric ({filename})', 'untyped')
                image_metric = Metric('my_metric_image_info', 'My custom metric - Image Information', 'untyped')

                # Read the JSON data from the file
                with open(json_path, 'r') as file:
                    data = json.load(file)

                # Extract the relevant data for your metrics
                replicas_value = data['replicas']
                risk_score_value = data['riskScore']
                deployment_name = data['name']
                namespace_name = data['namespace']
                container_image_name = data['containers'][0]['image']['name']['fullName']

 

                # Prepare the labels
                labels = {'namespace': namespace_name, 'deployment': deployment_name, 'container_image': container_image_name}

                # Add the metric data
                flatten_json(data, metric, labels=labels)

                # Yield the metric
                yield metric

def flatten_json(data, metric, prefix='', labels={}):
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                flatten_json(value, metric, f'{prefix}_{key}' if prefix else key, labels)
            elif isinstance(value, (int, float)):
                metric.add_sample(f'stackrox_{prefix}', value=value, labels=labels)
    elif isinstance(data, list):
        for index, value in enumerate(data):
            flatten_json(value, metric, f'{prefix}_{index}', labels)
    elif isinstance(data, (int, float)):
        metric.add_sample(f'stackrox_{prefix}', value=data, labels={})


def sanitize_metric_name(name):
    # Replace invalid characters with underscores
    sanitized_name = re.sub(r'[^a-zA-Z0-9_:]', '_', name)
    # Remove the .json extension
    return sanitized_name[:-5] if sanitized_name.endswith('.json') else sanitized_name


def main():
    # Specify the directory path containing the JSON files
    json_dir = 'jsons'

    # Create an instance of the custom collector
    collector = MyCollector(json_dir)

    # Register the collector with the Prometheus registry
    REGISTRY.register(collector)

    # Start the HTTP server to expose the metrics
    start_http_server(8080)

    # Keep the main thread alive
    while True:
        time.sleep(1)


if __name__ == '__main__':
    main()

