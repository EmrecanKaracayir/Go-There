package com.sep.gothere.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sep.gothere.R
import com.sep.gothere.features.root_postlog.business.branch_business.ui.BusinessFragment
import com.sep.gothere.features.root_postlog.explore.branch_explore.ui.ExploreFragment
import com.sep.gothere.features.root_postlog.profile.branch_profile.ui.ProfileFragment
import com.sep.gothere.features.root_postlog.search.branch_search.ui.SearchFragment
import com.sep.gothere.features.root_prelog.welcome.leaf_signup.ui.SignUpFragment
import com.sep.gothere.features.root_prelog.welcome.branch_welcome.ui.WelcomeFragment
import com.sep.gothere.util.exhaustive

private const val BASE_FRAGMENT_CONTAINER = R.id.fragment_container

enum class NavigationTAG {
    TAG_WELCOME_FRAGMENT,
    TAG_SIGN_UP_FRAGMENT,
    TAG_EXPLORE_FRAGMENT,
    TAG_SEARCH_FRAGMENT,
    TAG_PROFILE_FRAGMENT,
    TAG_BUSINESS_FRAGMENT
}

private var welcomeFragment: WelcomeFragment? = null
private var signUpFragment: SignUpFragment? = null
private var exploreFragment: ExploreFragment? = null
private var searchFragment: SearchFragment? = null
private var profileFragment: ProfileFragment? = null
private var businessFragment: BusinessFragment? = null

private val fragmentStack = Arraylist

private val navigationStack = ArrayList<NavigationNode>()


fun loadSession(fragmentManager: FragmentManager) {
    TODO()
    welcomeFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_WELCOME_FRAGMENT.name) as WelcomeFragment?
    signUpFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_SIGN_UP_FRAGMENT.name) as SignUpFragment?
    exploreFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_EXPLORE_FRAGMENT.name) as ExploreFragment?
    searchFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_SEARCH_FRAGMENT.name) as SearchFragment?
    profileFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_PROFILE_FRAGMENT.name) as ProfileFragment?
    businessFragment =
        fragmentManager.findFragmentByTag(NavigationTAG.TAG_BUSINESS_FRAGMENT.name) as BusinessFragment?
    navigateToThis(fragmentManager, -----------------, false)
}

private fun synchronizeNavigationObjects(fragmentManager: FragmentManager) {

}

fun navigateToRoot(
    fragmentManager: FragmentManager,
    destinationRoot: NavigationInfoProvider.NavigationRoot
) {
    when (destinationRoot) {
        NavigationInfoProvider.NavigationRoot.ROOT_PRELOG -> {
            createPreLogNavigationRoot(fragmentManager)
            navigateToThis(fragmentManager, welcomeFragment!!, true)
            destroyPostLogNavigationRoot(fragmentManager)
        }
        NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG -> {
            createPostLogNavigationRoot(fragmentManager)
            navigateToThis(fragmentManager, exploreFragment!!, true)
            destroyPreLogNavigationRoot(fragmentManager)
        }
    }.exhaustive
}

fun navigateToBranch(
    fragmentManager: FragmentManager, destinationBranch: NavigationInfoProvider.NavigationBranch
) {
    val source = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    if ((source as NavigationInfoProvider).getNavigationBranch() == destinationBranch) return
    if ((source as NavigationInfoProvider).getNavigationRoot() != destinationBranch.root) return
    val destination = navigationStack.findLast { navigationNode ->
        navigationNode.isActive && navigationNode.location.getNavigationBranch() == destinationBranch
    }?.location ?: createBranchOwner(fragmentManager, destinationBranch)
    navigateToThis(fragmentManager, (destination as Fragment), false)
}

/** Creates the branch owner based on the given branch */
private fun createBranchOwner(
    fragmentManager: FragmentManager,
    branch: NavigationInfoProvider.NavigationBranch
): NavigationInfoProvider {
    when (branch) {
        NavigationInfoProvider.NavigationBranch.BRANCH_WELCOME -> {
            welcomeFragment = WelcomeFragment()
            addDetachDestination(
                fragmentManager,
                welcomeFragment!!,
                NavigationTAG.TAG_WELCOME_FRAGMENT
            )
            navigationStack.add(
                NavigationNode(
                    welcomeFragment as NavigationInfoProvider,
                    isActive = true
                )
            )
            return welcomeFragment as WelcomeFragment
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE -> {
            exploreFragment = ExploreFragment()
            addDetachDestination(
                fragmentManager,
                exploreFragment!!,
                NavigationTAG.TAG_EXPLORE_FRAGMENT
            )
            navigationStack.add(
                NavigationNode(
                    exploreFragment as NavigationInfoProvider,
                    isActive = true
                )
            )
            return exploreFragment as ExploreFragment
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH -> {
            searchFragment = SearchFragment()
            addDetachDestination(
                fragmentManager,
                searchFragment!!,
                NavigationTAG.TAG_SEARCH_FRAGMENT
            )
            navigationStack.add(
                NavigationNode(
                    searchFragment as NavigationInfoProvider,
                    isActive = true
                )
            )
            return searchFragment as SearchFragment
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE -> {
            profileFragment = ProfileFragment()
            addDetachDestination(
                fragmentManager,
                profileFragment!!,
                NavigationTAG.TAG_PROFILE_FRAGMENT
            )
            navigationStack.add(
                NavigationNode(
                    profileFragment as NavigationInfoProvider,
                    isActive = true
                )
            )
            return profileFragment as ProfileFragment
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS -> {
            businessFragment = BusinessFragment()
            addDetachDestination(
                fragmentManager,
                businessFragment!!,
                NavigationTAG.TAG_BUSINESS_FRAGMENT
            )
            navigationStack.add(
                NavigationNode(
                    businessFragment as NavigationInfoProvider,
                    isActive = true
                )
            )
            return businessFragment as BusinessFragment
        }
    }
}

fun getCurrentBranch(fragmentManager: FragmentManager): NavigationInfoProvider.NavigationBranch {
    return (fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER) as NavigationInfoProvider).getNavigationBranch()
}

/** Function to provide common backward navigation. Tries to find best destination that
 * makes the most sense. It will search for destinations that are shares the same branch.
 * It also animates between source and destination.
 * After the transaction, based on source's caching strategy it removes/detaches the source.
 * - If the source is already the branch owner, it doesn't navigate.
 * - If destination not found, it doesn't navigate. */
fun navigateToPrevious(
    fragmentManager: FragmentManager,
) {
    val source: Fragment? = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    if ((source as NavigationInfoProvider).isNavigationBranchOwner()) return
    val destination = navigationStack.findLast { navigationNode ->
        navigationNode.location != (source as NavigationInfoProvider) && (navigationNode.location.getNavigationBranch() == (source as NavigationInfoProvider).getNavigationBranch())
    } ?: return
    val transaction = fragmentManager.beginTransaction().setCustomAnimations(
        R.anim.enter_from_left,
        R.anim.exit_to_right,
        R.anim.enter_from_left,
        R.anim.exit_to_right,
    )
    transaction.attach(destination.location as Fragment)
    val shouldCache = (source as NavigationInfoProvider).cacheOnBackPressed()
    if (shouldCache) {
        transaction.detach(source).setReorderingAllowed(true).commitNow()
        destination.isActive = false
    }
    else {
        transaction.remove(source).setReorderingAllowed(true).commitNow()
        fragmentManager.popBackStackImmediate()
        navigationStack.remove(destination)

    }
}

/** Function to provide common forward navigation.
 * If destination object is null. Function creates the destination.
 * @param navigationTAG defines the destination.
 * @param animate determines whether the transition applies an animation.*/
fun navigateToNext(
    fragmentManager: FragmentManager,
    navigationTAG: NavigationTAG,
    animate: Boolean
) {
    val source = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    when (navigationTAG) {
        NavigationTAG.TAG_WELCOME_FRAGMENT -> {
            if (welcomeFragment == null) {
                welcomeFragment = WelcomeFragment()
                addDetachDestination(fragmentManager, welcomeFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, welcomeFragment!!, animate)
        }
        NavigationTAG.TAG_SIGN_UP_FRAGMENT -> {
            if (signUpFragment == null) {
                signUpFragment = SignUpFragment()
                addDetachDestination(fragmentManager, signUpFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, signUpFragment!!, animate)
        }
        NavigationTAG.TAG_EXPLORE_FRAGMENT -> {
            if (exploreFragment == null) {
                exploreFragment = ExploreFragment()
                addDetachDestination(fragmentManager, exploreFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, exploreFragment!!, animate)
        }
        NavigationTAG.TAG_SEARCH_FRAGMENT -> {
            if (searchFragment == null) {
                searchFragment = SearchFragment()
                addDetachDestination(fragmentManager, searchFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, searchFragment!!, animate)
        }
        NavigationTAG.TAG_PROFILE_FRAGMENT -> {
            if (profileFragment == null) {
                profileFragment = ProfileFragment()
                addDetachDestination(fragmentManager, profileFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, profileFragment!!, animate)
        }
        NavigationTAG.TAG_BUSINESS_FRAGMENT -> {
            if (businessFragment == null) {
                businessFragment = BusinessFragment()
                addDetachDestination(fragmentManager, businessFragment!!, navigationTAG)
            }
            navigateToThis(fragmentManager, source, businessFragment!!, animate)
        }
    }.exhaustive
}

/** Navigates to the given destination object.
 * @param animate determines whether the transition applies an animation.
 * */
private fun navigateToThis(
    fragmentManager: FragmentManager,
    destination: Fragment,
    animate: Boolean
) {
    val source = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    val transaction = fragmentManager.beginTransaction()
    if (animate) {
        transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        )
    }
    if (source != null) transaction.detach(source)
    transaction.attach(destination).setReorderingAllowed(true).commitNow()
    navigationStack.add(
        NavigationNode(
            destination as NavigationInfoProvider,
            isActive = true
        )
    )
}

/** Adds & Hides given destination object. */
private fun addDetachDestination(
    fragmentManager: FragmentManager,
    destination: Fragment,
    navigationTAG: NavigationTAG
) {
    fragmentManager.beginTransaction().add(BASE_FRAGMENT_CONTAINER, destination, navigationTAG.name)
        .detach(destination).commitNow()
}

/** Adds & Detaches all PRELOG root navigation components. */
private fun createPreLogNavigationRoot(fragmentManager: FragmentManager) {
    welcomeFragment = WelcomeFragment()
    fragmentManager.beginTransaction()
        .add(BASE_FRAGMENT_CONTAINER, welcomeFragment!!, NavigationTAG.TAG_WELCOME_FRAGMENT.name)
        .detach(welcomeFragment!!).setReorderingAllowed(true).commitNow()
}

/** Removes all PRELOG root navigation components. */
private fun destroyPreLogNavigationRoot(fragmentManager: FragmentManager) {
    val fragmentsToDispose = ArrayList<Fragment>()
    for (fragment in fragmentManager.fragments)
        if (fragment is NavigationInfoProvider)
            if (fragment.getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_PRELOG)
                fragmentsToDispose.add(fragment)
    for (fragment in fragmentsToDispose) fragmentManager.beginTransaction().remove(fragment)
        .commitNow()
}

/** Adds & Detaches all POSTLOG root navigation components. */
private fun createPostLogNavigationRoot(fragmentManager: FragmentManager) {
    exploreFragment = ExploreFragment()
    searchFragment = SearchFragment()
    profileFragment = ProfileFragment()
    businessFragment = BusinessFragment()

    fragmentManager.beginTransaction()
        .add(BASE_FRAGMENT_CONTAINER, exploreFragment!!, NavigationTAG.TAG_EXPLORE_FRAGMENT.name)
        .detach(exploreFragment!!)
        .add(BASE_FRAGMENT_CONTAINER, searchFragment!!, NavigationTAG.TAG_SEARCH_FRAGMENT.name)
        .detach(searchFragment!!)
        .add(BASE_FRAGMENT_CONTAINER, profileFragment!!, NavigationTAG.TAG_PROFILE_FRAGMENT.name)
        .detach(profileFragment!!)
        .add(BASE_FRAGMENT_CONTAINER, businessFragment!!, NavigationTAG.TAG_BUSINESS_FRAGMENT.name)
        .detach(businessFragment!!)
        .setReorderingAllowed(true).commitNow()
}

/** Removes all POSTLOG root navigation components */
private fun destroyPostLogNavigationRoot(fragmentManager: FragmentManager) {
    val fragmentsToDispose = ArrayList<Fragment>()
    for (fragment in fragmentManager.fragments)
        if (fragment is NavigationInfoProvider) {
            if (fragment.getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG)
                fragmentsToDispose.add(fragment)
        }
    for (fragment in fragmentsToDispose) fragmentManager.beginTransaction().remove(fragment)
        .commitNow()
}