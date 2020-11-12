#!/bin/bash
ps -ef|grep WeBankBlockchain-Data-Reconcile |grep -v grep| awk '{print $2}'|xargs kill -9