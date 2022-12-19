package com.sep.gothere.navigation

interface NavigationInfoProvider {

    fun getNavigationRoot(): NavigationRoot

    fun getNavigationBranch(): NavigationBranch

    fun isNavigationBranchOwner(): Boolean

    fun cacheOnBackPressed(): Boolean

    enum class NavigationRoot {
        ROOT_PRELOG,
        ROOT_POSTLOG
    }

    enum class NavigationBranch(val root: NavigationRoot) {
        BRANCH_WELCOME(NavigationRoot.ROOT_PRELOG),
        BRANCH_EXPLORE(NavigationRoot.ROOT_POSTLOG),
        BRANCH_SEARCH(NavigationRoot.ROOT_POSTLOG),
        BRANCH_PROFILE(NavigationRoot.ROOT_POSTLOG),
        BRANCH_BUSINESS(NavigationRoot.ROOT_POSTLOG)
    }
}