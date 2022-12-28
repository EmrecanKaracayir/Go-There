package com.sep.gothere.navigation

interface NavigationInfoProvider {

    fun getNavigationRoot(): NavigationRoot

    fun getNavigationBranch(): NavigationBranch

    fun getNavigationTag(): NavigationTag

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

    enum class NavigationTag {
        TAG_WELCOME_FRAGMENT,
        TAG_SIGN_UP_FRAGMENT,
        TAG_EXPLORE_FRAGMENT,
        TAG_SEARCH_FRAGMENT,
        TAG_PROFILE_FRAGMENT,
        TAG_BUSINESS_FRAGMENT,
        TAG_BUSINESS_PUT_FRAGMENT,
        TAG_BUSINESS_PROFILE_FRAGMENT_BUSINESS,
        TAG_BUSINESS_PROFILE_FRAGMENT_EXPLORE,
        TAG_BUSINESS_PROFILE_FRAGMENT_SEARCH,
    }
}