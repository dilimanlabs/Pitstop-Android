package com.dilimanlabs.pitstop.ui.explore;

import com.dilimanlabs.pitstop.PitstopModule;

import dagger.Module;

@Module(
        injects = ExploreFragment.class,
        addsTo = PitstopModule.class,
        complete = false
)
public class ExploreFragmentModule {

}
