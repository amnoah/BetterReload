# api

This module contains the actual event. It should remain unchanged unless absolutely necessary so backwards compatibility
is maintained.

The purpose of separating it from my plugin implementation is so that any person can implement the ReloadEvent, 
whether they are calling it or listening it, without requiring them to use my implementation of a calling system. 
My implementation of the plugin portion is simply my implementation - not the de facto implementation.