package ndy.api.resources

import io.ktor.resources.*

@Resource("/articles")
class Articles(
        val tag: String? = null,
        val author: String? = null,
        val favorited: String? = null,
        val limit: Int = 20,
        val offset: Int = 0
) {
    @Resource("/feed")
    class Feed(val parent: Articles)

    @Resource("/{slug}")
    class Slug(val parent: Articles, val slug: String) {

        @Resource("/comments")
        class Comments(val parent: Slug) {

            @Resource("/{id}")
            class Id(val parent: Comments, val id: Int)
        }

        @Resource("/favorite")
        class Favorite(val parent: Slug)
    }
}
