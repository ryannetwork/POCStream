using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ASPNETCoreAngularJWT.Models;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace ASPNETCoreAngularJWT.Controllers
{
    [Produces("application/json")]
    [Route("api/Stream")]
    public class StreamController : Controller
    {
        // GET: api/Stream
        [HttpGet]
        public IEnumerable<string> Get()
        {
            return new string[] { "value1", "value2" };
        }

        // GET: api/Stream/5
        [HttpGet("{id}", Name = "Get")]
        public string Get(int id)
        {
            return "value";
        }
        
        // POST: api/Stream
        [HttpPost]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public IActionResult Post([FromBody]Members request)
        {
            string kafkaEndpoint = "127.0.0.1:9092";
            string kafkaTopic = "test";
            string member = request.Member;

            var producerConfig = new Dictionary<string, object> { { "bootstrap.servers", kafkaEndpoint } };
            using (var producer = new Producer<Null, string>(producerConfig, null, new StringSerializer(Encoding.UTF8)))
            {
                //Console.WriteLine("sending kafka request");
                var result = producer.ProduceAsync(kafkaTopic, null, member).GetAwaiter().GetResult();
                //Console.WriteLine("sent kafka request");
            }
            return Ok();
           
        }
        
        // PUT: api/Stream/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody]string value)
        {
        }
        
        // DELETE: api/ApiWithActions/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
