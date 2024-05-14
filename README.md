cs122bProject

Project 3:
Demo:

Stored Procedure: cs122b-project1-api-example/add-movie.sql

Inconsistencies:
Missing Field Requirements: There were missing fields that I needed to account for to be consistent with the attributes having NOT NULL in the schema.
The category codes also didn't aline with the existing genres so I used a hash map to organize them.

Optimization Strategies:
I used Batch Loading and Hash Maps to improve the efficiency of parsing. Batch loading doesn't load all the memory at once which helps reduce memory usage and hash maps have faster lookups, insertions, and deletions than lists. 
