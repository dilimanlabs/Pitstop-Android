package com.dilimanlabs.pitstop.ui.common;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.dilimanlabs.pitstop.Pitstop;

import java.util.List;

import dagger.ObjectGraph;

public abstract class BaseActionBarActivity extends ActionBarActivity{
    private ObjectGraph objectGraph;

    protected abstract List<Object> getModules();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        objectGraph = ((Pitstop) getApplication()).createScopedGraph(getModules().toArray());
        objectGraph.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override protected void onDestroy(){
        super.onDestroy();
        objectGraph = null;
    }
}
