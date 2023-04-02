package ndy.api.routers

import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.property.arbitrary.orNull
import ndy.api.dto.RegistrationRequest
import ndy.api.dto.UserUpdateRequest
import ndy.test.generator.ProfileArbs.bioValueArb
import ndy.test.generator.ProfileArbs.imageFullPathArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.registerArb
import ndy.test.spec.BaseSpec

class UserRoutesTest : BaseSpec(RequestArb, body = {

    /*integrationTest("signup") {
        checkAll<RegistrationRequest> { request ->
            // request
            val response = client.post(Users()) {
                setBody(mapOf("user" to request))
            }

            // assert
            response shouldHaveStatus Created
            assertSoftly(response.extract<UserResponse>("user")) {
                it.token shouldBe null
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }

    integrationTest("login") {
        checkAll<RegistrationRequest> { request ->
            // setup
            registerUser(request)

            // request
            val response = client.post(Users.Login()) {
                setBody(mapOf("user" to LoginRequest(request.email, request.password)))
            }

            // assert
            response shouldHaveStatus OK
            assertSoftly(response.extract<UserResponse>("user")) {
                it.token shouldNotBe null
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }


    integrationTest("get user") {
        checkAll<RegistrationRequest> { request ->
            // setup
            registerUser(request)
            val token = login(request)

            // request
            val response = client.get(User()) {
                authToken(token)
            }

            // assert
            response shouldHaveStatus OK
            assertSoftly(response.extract<UserResponse>("user")) {
                it.token shouldBe token
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }

    integrationTest("update user") {
        checkAll<RegistrationRequest, UserUpdateRequest> { registrationRequest, updateRequest ->
            // setup
            registerUser(registrationRequest)
            updateRequest.username?.let { assumeNotDuplicated(it) }
            val token = login(registrationRequest)

            // request
            val response = client.put(User()) {
                authToken(token)
                setBody(mapOf("user" to updateRequest))
            }

            // assert
            response shouldHaveStatus OK
            assertSoftly(response.extract<UserResponse>("user")) {
                it.token shouldBe token
                it.email shouldBeUpdatedToIf (updateRequest.email isNotNullOr registrationRequest.email)
                it.username shouldBeUpdatedToIf (updateRequest.username isNotNullOr registrationRequest.username)
                it.bio shouldBe updateRequest.bio
                it.image shouldBe updateRequest.image
            }
        }
    }*/
})

object RequestArb : BeforeSpecListener {
    override suspend fun beforeSpec(spec: Spec) {
        registerArb<RegistrationRequest>(usernameValueArb, emailValueArb, passwordValueArb)
        registerArb<UserUpdateRequest>(
            emailValueArb.orNull(),
            passwordValueArb.orNull(),
            usernameValueArb.orNull(),
            bioValueArb.orNull(),
            imageFullPathArb.orNull(),
        )
    }
}
