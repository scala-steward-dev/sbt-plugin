/*
<<<<<<< HEAD
 * Copyright 2018-2019 scala-steward contributors
=======
 * Copyright 2018-2019 Scala Steward contributors
>>>>>>> upstream/topic/gitlab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalasteward.core.vcs

import org.scalasteward.core.application.Config
import org.scalasteward.core.util.HttpJsonClient
import org.scalasteward.core.github.GitHubSpecifics
import org.scalasteward.core.github.http4s.Http4sGitHubApiAlg
import org.scalasteward.core.gitlab.http4s.Http4sGitLabApiAlg
import org.scalasteward.core.gitlab.GitlabSpecifics
import org.scalasteward.core.vcs.data.AuthenticatedUser
import cats.effect.Sync
import org.scalasteward.core.application.SupportedVCS.GitHub
import org.scalasteward.core.application.SupportedVCS.Gitlab
import org.scalasteward.core.bitbucket.http4s.Http4sBitbucketApiAlg
import org.scalasteward.core.bitbucket.BitbucketSpecifics
import org.scalasteward.core.application.SupportedVCS.Bitbucket

class VCSSelection[F[_]: Sync](implicit client: HttpJsonClient[F], user: AuthenticatedUser) {
  private def github(config: Config): (Http4sGitHubApiAlg[F], GitHubSpecifics) = {
    import org.scalasteward.core.github.http4s.authentication.addCredentials

    val alg = new Http4sGitHubApiAlg[F](config.vcsApiHost, _ => addCredentials(user))
    val specifics = new GitHubSpecifics()
    (alg, specifics)
  }
  private def gitlab(config: Config): (Http4sGitLabApiAlg[F], GitlabSpecifics) = {
    import org.scalasteward.core.gitlab.http4s.authentication.addCredentials

    val alg = new Http4sGitLabApiAlg[F](config.vcsApiHost, user, _ => addCredentials(user))
    val specifics = new GitlabSpecifics()
    (alg, specifics)
  }

  private def bitbucket(config: Config): (Http4sBitbucketApiAlg[F], BitbucketSpecifics) = {
    import org.scalasteward.core.bitbucket.http4s.authentication.addCredentials

    val alg = new Http4sBitbucketApiAlg(config.vcsApiHost, user, _ => addCredentials(user))
    val specifics = new BitbucketSpecifics()
    (alg, specifics)
  }
  def build(config: Config): (VCSApiAlg[F], VCSSpecifics) = config.vcsType match {
    case GitHub    => github(config)
    case Gitlab    => gitlab(config)
    case Bitbucket => bitbucket(config)
  }
}
