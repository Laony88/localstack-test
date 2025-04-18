#!/bin/bash

set -e
export AWS_ACCESS_KEY_ID=000000000000 AWS_SECRET_ACCESS_KEY=000000000000

echo "Creating S3 bucket: my-test-bucket"
awslocal s3 mb s3://my-test-bucket

echo "Uploading sample.txt"
awslocal s3 cp /etc/localstack/init/sample.txt s3://my-test-bucket/
