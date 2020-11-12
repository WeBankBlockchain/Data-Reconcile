#!/bin/bash
ps -ef|grep bc-reconcile |grep -v grep| awk '{print $2}'|xargs kill -9