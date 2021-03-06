/*
 * Copyright © 2011-2013 the spray project <http://spray.io>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.httpx.unmarshalling

import scala.xml.NodeSeq
import org.specs2.mutable.Specification
import spray.http._
import MediaTypes._
import HttpCharsets._

class BasicUnmarshallersSpec extends Specification {

  "The StringUnmarshaller" should {
    "decode `text/plain` content in UTF-8 to Strings" in {
      HttpEntity("Hällö").as[String] === Right("Hällö")
    }
  }

  "The CharArrayUnmarshaller" should {
    "decode `text/plain` content in UTF-8 to char arrays" in {
      HttpEntity("Hällö").as[Array[Char]].right.get.mkString === "Hällö"
    }
  }

  "The NodeSeqUnmarshaller" should {
    "decode `text/xml` content in UTF-8 to NodeSeqs" in {
      HttpEntity(`text/xml`, "<int>Hällö</int>").as[NodeSeq].right.get.text === "Hällö"
    }
  }

  "Unmarshaller.forNonEmpty" should {
    "prevent the underlying unmarshaller from unmarshalling empty entities" in {
      implicit val um = Unmarshaller.forNonEmpty[String]
      HttpEntity.Empty.as[String] === Left(ContentExpected)
    }
  }

  "Unmarshaller.unmarshaller" should {
    "produce the correct unmarshaller" in {
      val unmarshaller = Unmarshaller.unmarshaller[String]

      unmarshaller(HttpEntity("Hällö")) === Right("Hällö")
    }
  }

  "Unmarshaller.unmarshal" should {
    "succeed when unmarshalling valid entities" in {
      Unmarshaller.unmarshal[String](HttpEntity("Hällö")) === Right("Hällö")
    }

    "fail when unmarshalling invalid entities" in {
      val Left(UnsupportedContentType(msg)) = Unmarshaller.unmarshal[FormData](HttpEntity("Hällö"))
      msg === "Expected 'application/x-www-form-urlencoded'"
    }
  }

  "Unmarshaller.unmarshalUnsafe" should {
    "correctly unmarshal valid content" in {
      Unmarshaller.unmarshalUnsafe[String](HttpEntity("Hällö")) === "Hällö"
    }

    "throw an exception for invalid entities" in {
      Unmarshaller.unmarshalUnsafe[NodeSeq](HttpEntity("Hällö")) must throwA[RuntimeException]
    }
  }
}
